import { useState } from "react";
import Products from "../../../pages/Products";
import ProductCreateForm from "./ProductCreateForm";
import { ArrowLeftIcon, ChevronLeftIcon } from "@heroicons/react/20/solid";

function ProductList({categoryName, animalName}){
    const [editComponent, setEditComponent] = useState(false);
    const [productName, setProductName] = useState("");
    const [productDescription, setProductDescription] = useState("");
    const [productDetails, setProductDetails] = useState("");
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

    
    const onEdit = (data) => {
        setProductName(data.name || "");
        setProductDescription(data.description || "");
        setProductDetails(data.details || "");
        if(data.hasOwnProperty("color") && data.color !== undefined){
            setIsProductColorEnabled(true);
            setProductColor(data.color);
        }
        if(data.hasOwnProperty("size") && data.size !== undefined){
            setIsProductSizeEnabled(true);
            setProductSize(data.size);
        }
        if(data.hasOwnProperty("weight") && data.weight !== undefined && data.hasOwnProperty("weight_units") && data.weight_units !== undefined){
            setIsProductWeightEnabled(true);
            setProductWeightUnits(data.weight);
            setProductWeight(data.weight_units);
        }
        setProductStocksAvailable(data.productStocks || 1);
        setProductPrice(data.productPrice || 1);
        setIsFileUpload(!data.isExternalUpload);
        if(data.isExternalUpload){
            setImageUrls(data.imageUrls || []);
        }else{
            setImages(data.images || []);
        }
        setKeyFormKey(createFormKey + 1);
        if(data.hasOwnProperty("variationPrimaryId") && data.variationPrimaryId !== undefined){
            setVariationPrimaryId(data.variationPrimaryId);
        }
        setEditComponent(true);
    }

    const backToList = () => {
        setEditComponent(false);
    }


      return(
        <>
            {!editComponent ? 
             <Products isAdminPanelUsage={true} preventProductNavigation={true} hideTitleVisibility={true} hideSortVisibility={true} category_name_from_components={categoryName} animal_type_from_components={animalName} onEdit={onEdit} />
        :
        <>
        <div className="flex flex-row">
        <div className="flex items-center justify-center">
        <ArrowLeftIcon className="w-16 h-16 cursor-pointer" onClick={backToList} />
        </div>
        <div className="mx-80">
        <ProductCreateForm key={createFormKey} productName_Edit={productName} productDescription_Edit={productDescription} productDetails_Edit={productDetails} isProductColorEnabled_Edit={isProductColorEnabled} productColor_Edit={productColor} isProductSizeEnabled_Edit={isProductSizeEnabled} productSize_Edit={productSize} isProductWeightEnabled_Edit={isProductWeightEnabled} productWeightUnits_Edit={productWeightUnits} productWeight_Edit={productWeight} productStocksAvailable_Edit={productStocksAvailable} productPrice_Edit={productPrice} images_Edit={images} imageUrls_Edit={imageUrls} isFileUpload_Edit={isFileUpload} variationPrimaryId={variationPrimaryId} editMode={true} />
        </div>
        </div>
        </>
    }
           
        </>
      )
}

export default ProductList;