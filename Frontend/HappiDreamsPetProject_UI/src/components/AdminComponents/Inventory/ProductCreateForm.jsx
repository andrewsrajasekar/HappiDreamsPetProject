import { useEffect, useRef, useState } from "react";
import ImageThumbnail from "../ImageThumbnail";
import compressAndResizeImage from "../../../utils/ImageCompressAndResizer";
import { IMAGEFORMAT } from "../../../utils/ImageFormat";
import TabBar from "../../TabBar";
import { PRODUCT_WEIGHT_UNITS } from "../../../utils/ProductWeightUnits";
import Select from "react-select";
import colorName from 'color-name';
import { EditorState, convertToRaw, convertFromRaw, convertFromHTML, ContentState } from 'draft-js';
import { Editor } from "react-draft-wysiwyg";
import 'draft-js/dist/Draft.css';
import 'react-draft-wysiwyg/dist/react-draft-wysiwyg.css';
import { addImageFileToProduct, addImageUrlsToProduct, createProduct, deleteImageFromCategory, deleteImageFromProduct, deleteImagesFromProduct, updateProduct } from "../../../services/ApiClient";
import UINotification from "../../UINotification";

function ProductCreateForm({ selectedAnimal, selectedCategory, editMode, productId_Edit, productName_Edit, productDescription_Edit, productDetailsNonEditor_Edit, productDetails_Edit, isProductColorEnabled_Edit, productColor_Edit, isProductSizeEnabled_Edit, productSize_Edit, isProductWeightEnabled_Edit, productWeightUnits_Edit, productWeight_Edit, productStocksAvailable_Edit, productPrice_Edit, isFileUpload_Edit, images_Edit, imageUrls_Edit, variationPrimaryId, onEditDone }) {
  const convertTextToEditorComp = (data) => {
    const blocksFromHTML = convertFromHTML("<span>" + data + "</span>");
    const state = ContentState.createFromBlockArray(
      blocksFromHTML.contentBlocks,
      blocksFromHTML.entityMap,
    )
    return EditorState.createWithContent(state);
  }

  const [isEditComponent, setIsEditComponent] = useState(editMode !== undefined ? editMode : false);
  const isVariationCreateForm = isEditComponent ? variationPrimaryId !== undefined ? true : false : false;
  const fileInputRef = useRef(null);
  const [productName, setProductName] = useState(isEditComponent ? productName_Edit : "");
  const [productDescription, setProductDescription] = useState(isEditComponent ? productDescription_Edit : "");
  const [productDetailsEditor, setProductDetailsEditor] = useState(isEditComponent ? (productDetails_Edit && productDetails_Edit.trim().length > 0 ? EditorState.createWithContent(convertFromRaw(JSON.parse(productDetails_Edit))) : (productDetailsNonEditor_Edit && productDetailsNonEditor_Edit.trim().length > 0 ? convertTextToEditorComp(productDetailsNonEditor_Edit) : EditorState.createEmpty())) : EditorState.createEmpty());
  const [isProductColorEnabled, setIsProductColorEnabled] = useState(isEditComponent ? isProductColorEnabled_Edit : false);
  const [productColor, setProductColor] = useState(isEditComponent ? productColor_Edit : "");
  const [isProductSizeEnabled, setIsProductSizeEnabled] = useState(isEditComponent ? isProductSizeEnabled_Edit : false);
  const [productSize, setProductSize] = useState(isEditComponent ? productSize_Edit : "");
  const [isProductWeightEnabled, setIsProductWeightEnabled] = useState(isEditComponent ? isProductWeightEnabled_Edit : false);
  const [productWeightUnits, setProductWeightUnits] = useState(isEditComponent ? productWeightUnits_Edit : "");
  const [productWeight, setProductWeight] = useState(isEditComponent ? productWeight_Edit : 1);
  const [productStocksAvailable, setProductStocksAvailable] = useState(isEditComponent ? productStocksAvailable_Edit : 1);
  const [productPrice, setProductPrice] = useState(isEditComponent ? productPrice_Edit : 1);
  const [images, setImages] = useState(isEditComponent ? images_Edit : []);
  const [imageUrls, setImageUrls] = useState(isEditComponent ? imageUrls_Edit : []);
  const [imageUrl, setImageUrl] = useState('');
  const [isFileUpload, setIsFileUpload] = useState(isEditComponent ? isFileUpload_Edit : true);
  const [addedImageUrlsInEdit, setAddedImageUrlsInEdit] = useState([]);
  const [deletedImageUrlsInEdit, setDeletedImageUrlsInEdit] = useState([]);
  const [addedImagesInEdit, setAddedImagesInEdit] = useState([]);
  const [deletedImagesInEdit, setDeletedImagesInEdit] = useState([]);
  const maxImages = 5;

  const getLengthOfSelectedText = () => {
    const currentSelection = productDetailsEditor.getSelection();
    const isCollapsed = currentSelection.isCollapsed();

    let length = 0;

    if (!isCollapsed) {
      const currentContent = productDetailsEditor.getCurrentContent();
      const startKey = currentSelection.getStartKey();
      const endKey = currentSelection.getEndKey();
      const startBlock = currentContent.getBlockForKey(startKey);
      const isStartAndEndBlockAreTheSame = startKey === endKey;
      const startBlockTextLength = startBlock.getLength();
      const startSelectedTextLength = startBlockTextLength - currentSelection.getStartOffset();
      const endSelectedTextLength = currentSelection.getEndOffset();
      const keyAfterEnd = currentContent.getKeyAfter(endKey);
      console.log(currentSelection)
      if (isStartAndEndBlockAreTheSame) {
        length += currentSelection.getEndOffset() - currentSelection.getStartOffset();
      } else {
        let currentKey = startKey;

        while (currentKey && currentKey !== keyAfterEnd) {
          if (currentKey === startKey) {
            length += startSelectedTextLength + 1;
          } else if (currentKey === endKey) {
            length += endSelectedTextLength;
          } else {
            length += currentContent.getBlockForKey(currentKey).getLength() + 1;
          }

          currentKey = currentContent.getKeyAfter(currentKey);
        };
      }
    }

    return length;
  }

  const handleBeforeInput = () => {
    const currentContent = productDetailsEditor.getCurrentContent();
    const currentContentLength = currentContent.getPlainText('').length;
    const selectedTextLength = getLengthOfSelectedText();

    if (currentContentLength - selectedTextLength > 256 - 1) {
      console.log('you can type max ten characters');

      return 'handled';
    }
  }

  const handlePastedText = (pastedText) => {
    const currentContent = productDetailsEditor.getCurrentContent();
    const currentContentLength = currentContent.getPlainText('').length;
    const selectedTextLength = getLengthOfSelectedText();

    if (currentContentLength + pastedText.length - selectedTextLength > 256) {
      console.log('you can type max ten characters');

      return 'handled';
    }
  }


  const handleImageChange = async (event) => {
    const selectedFile = event.target.files[0];
    const compressedImage = await compressAndResizeImage(selectedFile, 400, 400, IMAGEFORMAT.PNG);
    const image = {
      id: isEditComponent && isFileUpload_Edit ? images_Edit.length + 1 : images.length + 1,
      name: selectedFile.name,
      url: compressedImage,
    };
    setImages([...images, image]);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
    if (isEditComponent) {
      setAddedImagesInEdit([...addedImagesInEdit, image]);
    }
  };

  const handleEditorChange = (editorState) => {
    setProductDetailsEditor(editorState);
  };

  const isSaveEnabled = () => {
    if (productName.trim() === "" || productDescription.trim() === "" || productDetailsEditor.getCurrentContent().getPlainText('').length <= 0) {
      return false;
    }
    // || !checkIfColorIsValid(productColor)
    if (isProductColorEnabled && (productColor.trim() === "")) {
      return false;
    }
    if (isProductSizeEnabled && productSize.trim() === "") {
      return false;
    }
    if (isProductWeightEnabled && productWeightUnits === "") {
      return false;
    }
    if (isProductWeightEnabled && productWeightUnits !== "" && productWeight <= 0) {
      return false;
    }
    if (productStocksAvailable <= 0) {
      return false;
    }
    if (productPrice <= 0) {
      return false;
    }
    if (isFileUpload) {
      if (!(images.length > 0 && images.length <= maxImages)) {
        return false;
      }
    } else {
      if (!(imageUrls.length > 0 && imageUrls.length <= maxImages)) {
        return false;
      }
    }
    return true;
  }

  const getProductWeightUnits = () => {
    let productWeightUnits = [];
    Object.values(PRODUCT_WEIGHT_UNITS).map((data, index) => {
      productWeightUnits.push({ id: index, label: data });
    })
    return productWeightUnits;
  }

  const handleImageUrlChange = (event) => {
    const url = event.target.value;
    setImageUrl(url);
  };

  const handleDeleteImage = (imageId) => {
    if (isEditComponent && isFileUpload_Edit) {
      let data = images_Edit.filter((image) => image.id === imageId);
      if (data && Array.isArray(data) && data.length > 0) {
        let oldData = images_Edit.filter((image) => image.id === data[0].id);
        if (oldData && Array.isArray(oldData) && oldData.length > 0) {
          setDeletedImagesInEdit(prevDeletedImagesInEdit => ([
            ...prevDeletedImagesInEdit,
            oldData[0]
          ]));
        }
      }
    }
    setImages(images.filter((image) => image.id !== imageId));
  }

  const addImageUrl = (event) => {
    event.preventDefault();
    setImageUrls([...imageUrls, imageUrl]);
    setImageUrl("");
    setAddedImageUrlsInEdit([...addedImageUrlsInEdit, imageUrl]);
  }

  const removeImageUrl = (index, event) => {
    event.preventDefault();
    let localImageUrls = [...imageUrls];
    let data = localImageUrls.splice(index, 1);
    setImageUrls(localImageUrls);
    if (isEditComponent && !isFileUpload_Edit && data[0].hasOwnProperty("id")) {
      let oldData = imageUrls_Edit.filter((image) => image.id === data[0].id);
      if (oldData && Array.isArray(oldData) && oldData.length > 0) {
        setDeletedImageUrlsInEdit(prevDeletedImagesInEdit => ([
          ...prevDeletedImagesInEdit,
          oldData[0]
        ]))
      }
    }
  }

  const handleInputChange = () => {
    setIsFileUpload(!isFileUpload);
    //setImages([]);
    setImageUrl('');
    //setImageUrls([]);
  };

  const handleProductUnitChange = (selectedOption) => {

    setProductWeightUnits(selectedOption);
    setProductWeight(1);
  }

  const customStyles = {
    control: (provided) => ({
      ...provided,
      background: 'white',
      cursor: 'pointer'
    }),
    option: (provided, state) => ({
      ...provided,
      cursor: state.isDisabled ? 'not-allowed' : 'pointer'
    })
  };

  const handleProductWeightChange = (event) => {
    const value = event.target.value;
    if (/^[0-9.]*$/.test(value)) {
      let weight = Number(value);
      if (isNaN(weight)) {
        return;
      }
      if (value === "") {
        setProductWeight(value);
        return;
      }
      if (weight <= 0) {
        return;
      }
      if (productWeightUnits.label === PRODUCT_WEIGHT_UNITS.GRAM || productWeightUnits.label === PRODUCT_WEIGHT_UNITS.MILLILITER) {
        if (weight < 1 || weight > 999.9999999999999) {
          return;
        }
      }
      setProductWeight(value);
    }
  };

  const handleProductPrice = (event) => {
    const value = event.target.value;
    if (/^[0-9.]*$/.test(value)) {
      let weight = Number(value);
      if (isNaN(weight)) {
        return;
      }
      if (value === "") {
        setProductPrice(value);
        return;
      }
      if (weight <= 0) {
        return;
      }
      setProductPrice(value);
    }
  };

  const handleProductStocks = (event) => {
    const value = event.target.value;
    if (/^[0-9]*$/.test(value)) {
      let stocks = Number(value);
      if (isNaN(stocks)) {
        return;
      }
      if (value === "") {
        setProductStocksAvailable(value);
        return;
      }
      if (stocks <= 0) {
        return;
      }
      setProductStocksAvailable(value);
    }
  };

  const handleProductColor = (event) => {
    const value = event.target.value;
    if (/\d/.test(value)) {
      return;
    }
    setProductColor(value);
  }

  // const checkIfColorIsValid = () => {
  //   const lowercaseColor = productColor.toLowerCase();
  //   if(!(lowercaseColor in colorName)){
  //     setProductColor("");
  //   }
  // }

  const getTextDataFromDetails = () => {
    const blocks = convertToRaw(productDetailsEditor.getCurrentContent()).blocks;
    const mappedBlocks = blocks.map(
      block => (!block.text.trim() && "\n") || block.text
    );

    let newText = "";
    for (let i = 0; i < mappedBlocks.length; i++) {
      const block = mappedBlocks[i];

      // handle last block
      if (i === mappedBlocks.length - 1) {
        newText += block;
      } else {
        // otherwise we join with \n, except if the block is already a \n
        if (block === "\n") newText += block;
        else newText += block + "\n";
      }
    }
    return newText;
  }

  const isMaxLengthNotAppliedForProductUnits = () => {
    return !(productWeightUnits.label === PRODUCT_WEIGHT_UNITS.GRAM || productWeightUnits.label === PRODUCT_WEIGHT_UNITS.MILLILITER);
  }

  const handleFormSubmit = async (event) => {
    event.preventDefault();
    let productInfo = {};
    productInfo.name = productName;
    productInfo.description = productDescription;
    productInfo.details = getTextDataFromDetails();
    productInfo.rich_text_details = JSON.stringify(convertToRaw(productDetailsEditor.getCurrentContent()));
    productInfo.stocks = Number(productStocksAvailable);
    productInfo.price = Number(productPrice);
    if (isProductColorEnabled) {
      productInfo.color = productColor;
    }
    if (isProductSizeEnabled) {
      productInfo.size = productSize;
    }
    if (isProductWeightEnabled) {
      productInfo.weightUnits = productWeightUnits.label.toLowerCase();
      productInfo.weight = Number(productWeight);
    }
    if (isEditComponent) {
      const updateProductResponse = await updateProduct(selectedAnimal.id, selectedCategory.id, productId_Edit, productInfo);
      if (updateProductResponse.isSuccess) {
        if (isFileUpload) {
          let deleteImageResponse = { isSuccess: true };
          if (isFileUpload_Edit) {
            if (deletedImagesInEdit && Array.isArray(deletedImagesInEdit) && deletedImagesInEdit.length > 0) {
              deleteImageResponse = await deleteImagesFromProduct(selectedAnimal.id, selectedCategory.id, productId_Edit, deletedImagesInEdit.map((image) => { return image.id }));
            }
          } else {
            if (imageUrls_Edit && Array.isArray(imageUrls_Edit) && imageUrls_Edit.length > 0) {
              deleteImageResponse = await deleteImagesFromProduct(selectedAnimal.id, selectedCategory.id, productId_Edit, imageUrls_Edit.map((image) => { return image.id }));
            }
          }
          if (deleteImageResponse.isSuccess) {
            let uploadImageResponse;
            let failedImages = [];
            for (let i = 0; i < images.length; i++) {
              if (!(addedImagesInEdit.contains(images))) {
                continue;
              }
              uploadImageResponse = await addImageFileToProduct(selectedAnimal.id, selectedCategory.id, productId_Edit, images[i]);
              if (!uploadImageResponse.isSuccess) {
                failedImages[i + 1];
              }
            }
            if (failedImages.length <= 0) {
              UINotification({ message: "Product Data Updated", type: "Success" });
              if(onEditDone && typeof onEditDone === "function"){
                onEditDone();
              }
            } else {
              UINotification({ message: "Issue Occured, while updating the following image number(s) " + failedImages.toString() + ", but the Product is saved, Kindly go to Edit Product and add Image", type: "Error" });
            }
          }else {
            UINotification({ message: "Issue Occured While updating the image, Kindly try again later.", type: "Error" });
          }

        } else {
          let deleteImageResponse = { isSuccess: true };
          if (isFileUpload_Edit) {
            if (images_Edit && Array.isArray(images_Edit) && images_Edit.length > 0) {
              deleteImageResponse = await deleteImagesFromProduct(selectedAnimal.id, selectedCategory.id, productId_Edit, images_Edit.map((image) => { return image.id }));
            }
          } else {
            if (deletedImageUrlsInEdit && Array.isArray(deletedImageUrlsInEdit) && deletedImageUrlsInEdit.length > 0) {
              deleteImageResponse = await deleteImagesFromProduct(selectedAnimal.id, selectedCategory.id, productId_Edit, deletedImageUrlsInEdit.map((image) => { return image.id }));
            }
          }

          if (deleteImageResponse.isSuccess) {
            let addImageUrlResponse = { isSuccess: true };
            if(addedImageUrlsInEdit && Array.isArray(addedImageUrlsInEdit) && addedImageUrlsInEdit.length > 0){
              addImageUrlResponse = await addImageUrlsToProduct(selectedAnimal.id, selectedCategory.id, productId_Edit, addedImageUrlsInEdit);
            }
            if (addImageUrlResponse.isSuccess) {
              UINotification({ message: "Product Data Updated", type: "Success" });
              if(onEditDone && typeof onEditDone === "function"){
                onEditDone();
              }
            } else {
              UINotification({ message: "Issue Occured, while updating the image URL, but the product is saved, Kindly go to Edit product and add Image URL", type: "Error" });
            }
          }else {
            UINotification({ message: "Issue Occured While updating the image, Kindly try again later.", type: "Error" });
          }
        }
      } else {
        UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
      }
    } else {
      const createProductResponse = await createProduct(selectedAnimal.id, selectedCategory.id, productInfo);
      if (createProductResponse.isSuccess) {
        if (isFileUpload) {
          let uploadImageResponse;
          let failedImages = [];
          for (let i = 0; i < images.length; i++) {
            uploadImageResponse = await addImageFileToProduct(selectedAnimal.id, selectedCategory.id, createProductResponse.successResponse.data.data.id, images[i]);
            if (!uploadImageResponse.isSuccess) {
              failedImages[i + 1];
            }
          }
          if (failedImages.length <= 0) {
            UINotification({ message: "Product Data Added", type: "Success" });
          } else {
            UINotification({ message: "Issue Occured, while adding the following image number(s) " + failedImages.toString() + ", but the Product is saved, Kindly go to Edit Product and add Image", type: "Error" });
          }

        } else {
          const addImageUrlResponse = await addImageUrlsToProduct(selectedAnimal.id, selectedCategory.id, createProductResponse.successResponse.data.data.id, imageUrls);
          if (addImageUrlResponse.isSuccess) {
            UINotification({ message: "Product Data Added", type: "Success" });
          } else {
            UINotification({ message: "Issue Occured, while adding the image URL, but the product is saved, Kindly go to Edit product and add Image URL", type: "Error" });
          }
        }
        setProductName("");
        setProductDescription("");
        setProductDetailsEditor(EditorState.createEmpty());
        setIsFileUpload(true);
        setImageUrl("");
        setImageUrls([]);
        setImages([]);
        setProductColor("");
        setIsProductColorEnabled(false);
        setProductSize("");
        setIsProductSizeEnabled(false);
        setProductWeight(1);
        setProductWeightUnits("");
        setIsProductWeightEnabled(false);
      } else {
        UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
      }
    }
  };

  return (
    <>
      <form onSubmit={handleFormSubmit}>
        <div className="mb-4">
          <label className="block text-gray-700 font-bold mb-2" htmlFor="productName">
            Product Name
          </label>
          <input
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="productName"
            type="text"
            placeholder="Enter Product Name"
            minLength={3}
            maxLength={50}
            value={productName}
            onChange={(e) => setProductName(e.target.value)}
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 font-bold mb-2" htmlFor="productDescription">
            Product Description
          </label>
          <textarea
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="productName"
            type="text"
            placeholder="Enter Product Description"
            minLength={3}
            maxLength={256}
            value={productDescription}
            onChange={(e) => setProductDescription(e.target.value)}
          />
        </div>
        <div className="mb-4" >
          <label className="block text-gray-700 font-bold mb-2" htmlFor="productDetails">
            Product Details
          </label>
          <div className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
            <Editor
              editorState={productDetailsEditor}
              onEditorStateChange={handleEditorChange}
              handleBeforeInput={handleBeforeInput}
              handlePastedText={handlePastedText}
            />
          </div>
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 font-bold mb-2" htmlFor="productColorEnabled">
            Enable Product Color
          </label>
          <div className="mr-auto">
            <div className="flex items-start">
              <TabBar removePadding={true} tabs={[{ "id": 0, "label": "NO", "handleOnClick": () => { return false; } }, { "id": 1, "label": "YES", "handleOnClick": () => { return true; } }]} manualSelectTabIndex={isProductColorEnabled_Edit ? 1 : -1} onTabClick={(value) => { setIsProductColorEnabled(value) }} />
            </div>
          </div>
        </div>
        {isProductColorEnabled &&
          <div className="mb-4">
            <label className="block text-gray-700 font-bold mb-2" htmlFor="productColor">
              Enter Product Color
            </label>
            <input
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              id="productColor"
              type="text"
              placeholder="Enter Product Color"
              minLength={1}
              value={productColor}
              onChange={handleProductColor}
            // onBlur={checkIfColorIsValid}
            />
          </div>
        }
        <div className="mb-4">
          <label className="block text-gray-700 font-bold mb-2" htmlFor="productSizeEnabled">
            Enable Product Size
          </label>
          <div className="mr-auto">
            <div className="flex items-start">
              <TabBar removePadding={true} tabs={[{ "id": 0, "label": "NO", "handleOnClick": () => { return false; } }, { "id": 1, "label": "YES", "handleOnClick": () => { return true; } }]} manualSelectTabIndex={isProductSizeEnabled_Edit ? 1 : -1} onTabClick={(value) => { setIsProductSizeEnabled(value) }} />
            </div>
          </div>
        </div>
        {isProductSizeEnabled &&
          <div className="mb-4">
            <label className="block text-gray-700 font-bold mb-2" htmlFor="productSize">
              Enter Product Size
            </label>
            <input
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              id="productSize"
              type="text"
              placeholder="Enter Product Size"
              minLength={1}
              value={productSize}
              onChange={(e) => setProductSize(e.target.value)}
            />
          </div>
        }
        <div className="mb-4">
          <label className="block text-gray-700 font-bold mb-2" htmlFor="productWeightEnabled">
            Enable Product Weight
          </label>
          <div className="mr-auto">
            <div className="flex items-start">
              <TabBar removePadding={true} tabs={[{ "id": 0, "label": "NO", "handleOnClick": () => { return false; } }, { "id": 1, "label": "YES", "handleOnClick": () => { return true; } }]} manualSelectTabIndex={isProductWeightEnabled_Edit ? 1 : -1} onTabClick={(value) => { setIsProductWeightEnabled(value) }} />
            </div>
          </div>
        </div>
        {isProductWeightEnabled &&
          <div className="mb-4">
            <label className="block text-gray-700 font-bold mb-2" htmlFor="productWeightUnits">
              Select Product Weight Units
            </label>
            <div className="mr-auto">
              <div className="flex items-start">
                <Select styles={customStyles} options={getProductWeightUnits()} placeholder={'Select an Unit'} onChange={handleProductUnitChange} getOptionValue={(option) => option.label} value={productWeightUnits !== "" ? productWeightUnits : null} />
              </div>
            </div>
          </div>
        }
        {isProductWeightEnabled && productWeightUnits !== "" &&
          <div className="mb-4">
            <label className="block text-gray-700 font-bold mb-2" htmlFor="productWeight">
              Enter Product Weight
            </label>
            <input
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              id="productWeight"
              type="text"
              placeholder="Enter Product Weight"
              minLength={1}
              maxLength={isMaxLengthNotAppliedForProductUnits ? null : 3}
              value={productWeight}
              onChange={handleProductWeightChange}
            />
          </div>
        }
        <div className="mb-4">
          <label className="block text-gray-700 font-bold mb-2" htmlFor="productPrice">
            Enter Product Price (₹)
          </label>
          <input
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="productPrice"
            type="text"
            placeholder="Enter Product Price"
            minLength={1}
            value={productPrice}
            onChange={handleProductPrice}
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 font-bold mb-2" htmlFor="productStocks">
            Enter Product Stocks Available
          </label>
          <input
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="productStocks"
            type="text"
            placeholder="Enter Product Stocks"
            minLength={1}
            value={productStocksAvailable}
            onChange={handleProductStocks}
          />
        </div>
        <div className="mb-4">
          <label className="block mb-2 text-sm font-bold text-gray-700" htmlFor="inputChoice">
            Choose Input Type
          </label>
          <select
            className="border border-gray-300 px-3 py-2 rounded-lg w-full"
            id="inputChoice"
            value={isFileUpload ? 'file' : 'url'}
            onChange={handleInputChange}
          >
            <option value="file">Upload File</option>
            <option value="url">Image URL</option>
          </select>
        </div>
        {isFileUpload ? (
          <div className="mb-4">
            <label className="block mb-2 text-sm font-bold text-gray-700" htmlFor="fileInput">
              Upload File
            </label>
            <input
              className="border border-gray-300 px-3 py-2 rounded-lg w-full"
              type="file"
              id="fileInput"
              accept="image/*"
              disabled={images.length >= maxImages}
              onChange={images.length >= maxImages ? null : handleImageChange}
              ref={fileInputRef}
            />
            {images.length > 0 &&
              <div className="flex items-center justify-center flex-row mt-5">
                {images.map((image, index) => {
                  return (
                    <div key={index}>
                      <ImageThumbnail
                        key={index}
                        image={image}
                        onDelete={handleDeleteImage}
                        showName={false}
                        removeWidth={true}
                      />
                    </div>
                  )
                })}
              </div>
            }
          </div>
        ) : (
          <div className="mb-4">
            <label className="block mb-2 text-sm font-bold text-gray-700" htmlFor="imageUrlInput">
              Image URL
            </label>
            <div className="flex flex-row">
              <input
                className="border border-gray-300 px-3 py-2 rounded-lg w-full"
                type="text"
                id="imageUrlInput"
                value={imageUrl}
                onChange={handleImageUrlChange}
              />
              <span>
                <button onClick={imageUrls.length >= maxImages || imageUrl.trim() === "" ? null : addImageUrl} disabled={imageUrls.length >= maxImages || imageUrl.trim() === ""} className={`text-white font-bold py-2 px-4 rounded ml-4 disabled:opacity-25 disabled:cursor-not-allowed bg-green-500 hover:bg-green-700`}>
                  Add
                </button>
              </span>
            </div>
            {imageUrls.length > 0 &&
              <span className="flex items-center justify-center flex-col mt-4 mb-4">
                {imageUrls.map((image, index) => {
                  return (
                    <div className="flex flex-row mt-2">

                      <span className="border border-gray-300 px-3 py-2 rounded-lg overflow-x-auto" style={{ maxWidth: '250px' }} key={index}>
                        {isEditComponent ? (image.hasOwnProperty("imageUrl") ? image.imageUrl : image)  : image}
                      </span>
                      <span className="flex items-center justify-center">
                        <button onClick={(event) => { removeImageUrl(index, event) }} className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded ml-2">
                          Remove
                        </button>
                      </span>
                    </div>
                  );
                })}
              </span>
            }

          </div>
        )}
        <button
          disabled={!isSaveEnabled()}
          className="bg-purple-600 hover:bg-purple-900 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline disabled:opacity-25 disabled:cursor-not-allowed"
          type="submit"
        >
          {isVariationCreateForm ? "Add Variation" : isEditComponent ? "Update" : "Save"}
        </button>
      </form>
    </>
  )
}

export default ProductCreateForm;