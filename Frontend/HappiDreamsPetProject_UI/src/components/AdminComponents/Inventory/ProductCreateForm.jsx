import { useRef, useState } from "react";
import ImageThumbnail from "../ImageThumbnail";
import compressAndResizeImage from "../../../utils/ImageCompressAndResizer";
import { IMAGEFORMAT } from "../../../utils/ImageFormat";
import TabBar from "../../TabBar";
import { PRODUCT_WEIGHT_UNITS } from "../../../utils/ProductWeightUnits";
import Select from "react-select";
import colorName from 'color-name';

function ProductCreateForm({editMode, productName_Edit, productDescription_Edit, productDetails_Edit, isProductColorEnabled_Edit, productColor_Edit, isProductSizeEnabled_Edit, productSize_Edit, isProductWeightEnabled_Edit, productWeightUnits_Edit, productWeight_Edit, productStocksAvailable_Edit, productPrice_Edit, isFileUpload_Edit, images_Edit, imageUrls_Edit, variationPrimaryId}){
    const [isEditComponent, setIsEditComponent] = useState(editMode !== undefined ? editMode : false);  
    const isVariationCreateForm = isEditComponent ? variationPrimaryId !== undefined ? true : false : false;
    const fileInputRef = useRef(null);
    const [productName, setProductName] = useState(isEditComponent ? productName_Edit : "");
    const [productDescription, setProductDescription] = useState(isEditComponent ? productDescription_Edit : "");
    const [productDetails, setProductDetails] = useState(isEditComponent ? productDetails_Edit : "");
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
    const maxImages = 5;
  
    const handleImageChange = async (event) => {
      const selectedFile = event.target.files[0];
      const compressedImage = await compressAndResizeImage(selectedFile, 400, 400, IMAGEFORMAT.PNG);
      const image = {
        id: images.length,
        name: selectedFile.name,
        url: compressedImage,
      };
      setImages([...images, image]);
      if (fileInputRef.current) {
        fileInputRef.current.value = "";
      }
    };

    const isSaveEnabled = () => {
      if(productName.trim() === "" || productDescription.trim() === "" || productDetails.trim() === ""){
        return false;
      }
      if(isProductColorEnabled && ( productColor.trim() === "" || !checkIfColorIsValid(productColor))){
        return false;
      }
      if(isProductSizeEnabled && productSize.trim() === ""){
        return false;
      }
      if(isProductWeightEnabled && productWeightUnits.trim() === "" ){
        return false;
      }
      if(isProductWeightEnabled && productWeightUnits.trim() !== "" && productWeight <= 0){
        return false;
      }
      if(productStocksAvailable <= 0){
        return false;
      }
      if(productPrice <= 0){
        return false;
      }
      if(isFileUpload){
        if(!(images.length > 0 && images.length <= maxImages)){
          return false;
        }
      }else{
        if(!(imageUrls.length > 0 && imageUrls.length <= maxImages)){
          return false;
        }
      }
      return true;
    }
  
    const getProductWeightUnits = () => {
      let productWeightUnits = [];
      Object.values(PRODUCT_WEIGHT_UNITS).map((data,index) => {
        productWeightUnits.push({id: index, label: data});
      })
      return productWeightUnits;
    }

    const handleImageUrlChange = (event) => {
      const url = event.target.value;
      setImageUrl(url);
    };

    const handleDeleteImage = (imageId) => {
        setImages(images.filter((image) => image.id !== imageId));
    }

    const addImageUrl = (event) => {
        event.preventDefault();
        setImageUrls([...imageUrls, imageUrl]);
        setImageUrl("");
    }

    const removeImageUrl = (index, event) => {
        event.preventDefault();
        let localImageUrls = [...imageUrls];
        localImageUrls.splice(index, 1);
        setImageUrls(localImageUrls);
    }

    const handleInputChange = () => {
        setIsFileUpload(!isFileUpload);
        setImages([]);
        setImageUrl('');
        setImageUrls([]);
    };

    const handleProductUnitChange = (selectedOption) => {
      debugger;
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
        if(isNaN(weight)){
          return;
        }
        if(value === ""){
          setProductWeight(value);
          return;
        }
        if(weight <= 0){
          return;
        }
        if(productWeightUnits.label === PRODUCT_WEIGHT_UNITS.GRAM || productWeightUnits.label === PRODUCT_WEIGHT_UNITS.MILLILITER){
          if(weight < 1 || weight > 999.9999999999999){
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
        if(isNaN(weight)){
          return;
        }
        if(value === ""){
          setProductPrice(value);
          return;
        }
        if(weight <= 0){
          return;
        }
        setProductPrice(value);
      }
    };

    const handleProductStocks = (event) => {
      const value = event.target.value;
      if (/^[0-9]*$/.test(value)) {
        let stocks = Number(value);
        if(isNaN(stocks)){
          return;
        }
        if(value === ""){
          setProductStocksAvailable(value);
          return;
        }
        if(stocks <= 0){
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

    const checkIfColorIsValid = () => {
      const lowercaseColor = productColor.toLowerCase();
      if(!(lowercaseColor in colorName)){
        setProductColor("");
      }
    }

    const isMaxLengthNotAppliedForProductUnits = () => {
      return !(productWeightUnits.label === PRODUCT_WEIGHT_UNITS.GRAM || productWeightUnits.label === PRODUCT_WEIGHT_UNITS.MILLILITER);
    }

    const handleFormSubmit = (event) => {
    event.preventDefault();

    // Process the chosen input (file or image URL) based on the user's choice
    if (isFileUpload) {
        // Handle file upload
        if (images) {
        // Upload the file
        console.log('Uploading file:', images);
        } else {
        // File not selected
        console.log('Please select a file.');
        }
    } else {
        // Handle image URL input
        if (imageUrl) {
        // Process the image URL
        console.log('Image URL:', imageUrl);
        } else {
        // Image URL not provided
        console.log('Please enter an image URL.');
        }
    }
    };

    return(
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
            <div className="mb-4">
                <label className="block text-gray-700 font-bold mb-2" htmlFor="productDetails">
                Product Details
                </label>
                <textarea
          className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
          id="productName"
          type="text"
          placeholder="Enter Product Description"
          minLength={3}
          maxLength={256}
          value={productDetails}
          onChange={(e) => setProductDetails(e.target.value)}
        />
            </div>
        <div className="mb-4">
            <label className="block text-gray-700 font-bold mb-2" htmlFor="productColorEnabled">
            Enable Product Color
            </label>
              <div className="mr-auto">
              <div className="flex items-start">
              <TabBar removePadding={true} tabs={[{"id": 0, "label": "NO", "handleOnClick": () => {return false;}},{"id": 1, "label": "YES", "handleOnClick": () => {return true;}}]} onTabClick={(value) => {setIsProductColorEnabled(value)}} />
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
          onBlur={checkIfColorIsValid}
        />
        </div>
        }
        <div className="mb-4">
            <label className="block text-gray-700 font-bold mb-2" htmlFor="productSizeEnabled">
            Enable Product Size
            </label>
              <div className="mr-auto">
              <div className="flex items-start">
              <TabBar removePadding={true} tabs={[{"id": 0, "label": "NO", "handleOnClick": () => {return false;}},{"id": 1, "label": "YES", "handleOnClick": () => {return true;}}]} onTabClick={(value) => {setIsProductSizeEnabled(value)}} />
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
              <TabBar removePadding={true} tabs={[{"id": 0, "label": "NO", "handleOnClick": () => {return false;}},{"id": 1, "label": "YES", "handleOnClick": () => {return true;}}]} onTabClick={(value) => {setIsProductWeightEnabled(value)}} />
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
              <Select styles={customStyles} options={getProductWeightUnits()} placeholder={'Select an Unit'} onChange={handleProductUnitChange}  getOptionValue={(option) => option.label} value={productWeightUnits !== "" ? productWeightUnits : null} />
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
                  return(
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
            <button onClick={imageUrls.length >= maxImages || imageUrl.trim() === "" ? null : addImageUrl} disabled={imageUrls.length >= maxImages || imageUrl.trim() === ""} className={`w-36 text-white font-bold py-2 px-4 rounded ml-4 disabled:opacity-25 disabled:cursor-not-allowed bg-green-500 hover:bg-green-700`}>
                Add
            </button>
          </span>
          </div>
          {imageUrls.length > 0 &&    
                    <span className="flex items-center justify-center flex-col mt-4 mb-4">
                        {imageUrls.map((image, index) => {
                            return(
                            <div className="flex flex-row mt-2">
                            
                            <span className="border border-gray-300 px-3 py-2 rounded-lg" key={index}>
                                {image}
                            </span>
                            <button onClick={(event) => {removeImageUrl(index, event)}} className="bg-red-500 w-36 hover:bg-red-700 text-white font-bold py-2 px-4 rounded ml-4">
                            Remove
                            </button>
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