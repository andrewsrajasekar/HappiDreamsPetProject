import { useState } from "react";
import Products from "../../../pages/Products";
import ProductCreateForm from "./ProductCreateForm";
import { ArrowLeftIcon, ChevronLeftIcon } from "@heroicons/react/20/solid";
import { getProduct } from "../../../services/ApiClient";
import UINotification from "../../UINotification";

function ProductList({selectedCategory, selectedAnimal}){
    const [editComponent, setEditComponent] = useState(false);
    const [productId, setProductId] = useState(-1);
    const [productName, setProductName] = useState("");
    const [productDescription, setProductDescription] = useState("");
    const [productDetails, setProductDetails] = useState("");
    const [productDetailsNonEditor, setProductDetailsNonEditor] = useState("");
    const [isProductDetailsEditorText, setIsProductDetailsEditorText] = useState(true);
    const [isProductColorEnabled, setIsProductColorEnabled] = useState(false);
    const [productColor, setProductColor] = useState("");
    const [isProductSizeEnabled, setIsProductSizeEnabled] = useState(false);
    const [productSize, setProductSize] = useState("");
    const [isProductWeightEnabled, setIsProductWeightEnabled] = useState(false);
    const [productWeightUnits, setProductWeightUnits] = useState("");
    const [productWeight, setProductWeight] = useState(1);
    const [productStocksAvailable, setProductStocksAvailable] = useState(1);
    const [productPrice, setProductPrice] = useState(1);
    const [images, setImages] = useState([]);
    const [imageUrls, setImageUrls] = useState([]);
    const [isFileUpload, setIsFileUpload] = useState(true);
    const [variationPrimaryId, setVariationPrimaryId] = useState(undefined);
    const [createFormKey, setKeyFormKey] = useState(1);
    const [editFormKey, setEditFormKey] = useState(1);

    
    const onEdit = async (data) => {
        let productId = data.id;
        let productInfo = await getProduct(selectedCategory.id, selectedAnimal.id, productId);
        if(productInfo.isSuccess){
            productInfo = productInfo.successResponse.data.data;
        }else{
            UINotification({message: "Issue Occured, Kindly try again later.", type: "Error"});
        }
        setProductId(productId);
        setProductName(productInfo.name || "");
        setProductDescription(productInfo.description || "");
        if(productInfo.richtextDetails && productInfo.richtextDetails.trim().length > 0 ){
            setIsProductDetailsEditorText(true);
            setProductDetails(productInfo.richtextDetails);
        }else{
            setIsProductDetailsEditorText(false);
            if(productInfo.details && productInfo.details.trim().length > 0){
                setProductDetailsNonEditor(productInfo.details);
            }else{
                setProductDetailsNonEditor("");
            }
            
        }
        if(productInfo.hasOwnProperty("color") && productInfo.color !== undefined){
            setIsProductColorEnabled(true);
            setProductColor(productInfo.color);
        }
        if(productInfo.hasOwnProperty("size") && productInfo.size !== undefined){
            setIsProductSizeEnabled(true);
            setProductSize(productInfo.size);
        }
        if(productInfo.hasOwnProperty("weight") && productInfo.weight !== undefined && productInfo.hasOwnProperty("weight_units") && productInfo.weight_units !== undefined){
            setIsProductWeightEnabled(true);
            setProductWeightUnits(productInfo.weight);
            setProductWeight(productInfo.weight_units);
        }
        setProductStocksAvailable(productInfo.productStocks || 1);
        setProductPrice(productInfo.productPrice || 1);
        let isImagesPresent = productInfo.images ? (Array.isArray(productInfo.images) ? productInfo.images.length > 0 : true ): false;
        let isExternalUrl = isImagesPresent ? productInfo.images[0].imageType === "external_url" : false;
        setIsFileUpload(!isExternalUrl);
        if(isImagesPresent){
            if(isExternalUrl){
                setImageUrls(productInfo.images);
            }else{
                setImages(productInfo.images);
            }
        }else{
            setImages([]);
        }
        setKeyFormKey(createFormKey + 1);
        if(productInfo.hasOwnProperty("variationPrimaryId") && productInfo.variationPrimaryId !== undefined){
            setVariationPrimaryId(productInfo.variationPrimaryId);
        }
        setEditComponent(true);
    }

    const backToList = () => {
        resetData();
        setEditComponent(false);
        setEditFormKey(editFormKey + 1);
        
    }

    const resetData = () => {
        setProductId(-1);
        setProductName("");
        setProductDescription("");
        setProductDetails("");
        setProductDetailsNonEditor("");
        setIsProductDetailsEditorText(true);
        setIsProductColorEnabled(false);
        setProductColor("");
        setIsProductSizeEnabled(false);
        setProductSize("");
        setIsProductWeightEnabled(false);
        setProductWeightUnits("");
        setProductWeight(1);
        setProductStocksAvailable(1);
        setProductPrice(1);
        setImages([]);
        setImageUrls([]);
        setIsFileUpload(true);
        setVariationPrimaryId(undefined);
    }


      return(
        <>
            {!editComponent ? 
             <Products key={editFormKey} isAdminPanelUsage={true} preventProductNavigation={true} hideTitleVisibility={true} hideSortVisibility={true} category_info_from_components={selectedCategory} animal_info_from_components={selectedAnimal} onEdit={onEdit} />
        :
        <>
        <div className="flex flex-row">
        <div className="flex items-center justify-center">
        <ArrowLeftIcon className="w-16 h-16 cursor-pointer" onClick={backToList} />
        </div>
        <div className="mx-80">
        <ProductCreateForm selectedAnimal={selectedAnimal} selectedCategory={selectedCategory} key={createFormKey} productId_Edit={productId} productName_Edit={productName} productDescription_Edit={productDescription} productDetailsNonEditor_Edit={isProductDetailsEditorText ? undefined : productDetailsNonEditor} productDetails_Edit={isProductDetailsEditorText ? productDetails : undefined} isProductColorEnabled_Edit={isProductColorEnabled} productColor_Edit={productColor} isProductSizeEnabled_Edit={isProductSizeEnabled} productSize_Edit={productSize} isProductWeightEnabled_Edit={isProductWeightEnabled} productWeightUnits_Edit={productWeightUnits} productWeight_Edit={productWeight} productStocksAvailable_Edit={productStocksAvailable} productPrice_Edit={productPrice} images_Edit={images} imageUrls_Edit={imageUrls} isFileUpload_Edit={isFileUpload} variationPrimaryId={variationPrimaryId} onEditDone={backToList} editMode={true} />
        </div>
        </div>
        </>
    }
           
        </>
      )
}

export default ProductList;