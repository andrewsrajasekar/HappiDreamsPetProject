import { useEffect, useState } from "react";
import VariationProducts from "./VariationProducts";
import Modal from "react-responsive-modal";
import { ArrowLeftIcon } from "@heroicons/react/24/solid";
import Product from "../pages/Product";
import Select from "react-select";
import { createProductVariation, deleteProductVariation, getAllAvailableProductsForVariation, getProductVariation } from "../services/ApiClient";
import UINotification from "./UINotification";

function Variation({animalId, categoryId, productId, isSizeDataPresent, isWeightDataPresent, isColorDataPresent}){
    const [bySizeProducts, setBySizeProducts] = useState([]);
    const [byColorProducts, setByColorProducts] = useState([]);
    const [byWeightProducts, setByWeightProducts] = useState([]);
    const [addVariantOpen, setAddVariantOpen] = useState(false);
    const [productDetailsView, setProductDetailsView] = useState(false);
    const [currentProductForView, setCurrentProductForView] = useState({});
    const [addType, setAddType] = useState("");
    const [productsForType, setProductsForType] = useState([]);
    const [isProductsForTypeFetchingDone, setIsProductsForTypeFetchingDone] = useState(true);
    const [selectedProductForType, setSelectedProductForType] = useState({});
    const [preventTriggerAPI, setPreventTriggerAPI] = useState(false);
    const [refreshVariationList, setRefreshVariationList] = useState(false);

const fetchProductVariation = async () => {
  setIsProductsForTypeFetchingDone(false);
    const variationInfo = await getProductVariation(animalId, categoryId, productId);
    if(variationInfo.isSuccess){
      if(variationInfo.successResponse.data.hasOwnProperty("weight_variant_info")){
        setByWeightProducts(variationInfo.successResponse.data.weight_variant_info);
      }else{
        setByWeightProducts([]);
      }
      if(variationInfo.successResponse.data.hasOwnProperty("size_variant_info")){
        setBySizeProducts(variationInfo.successResponse.data.size_variant_info);
      }else{
        setBySizeProducts([]);
      }
      if(variationInfo.successResponse.data.hasOwnProperty("color_variant_info")){
        setByColorProducts(variationInfo.successResponse.data.color_variant_info);
      }else{
        setByColorProducts([]);
      }
    }else{
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
    }
}

useEffect(() => {
  fetchProductVariation();
}, []);

useEffect(() => {
  if(refreshVariationList){
    setRefreshVariationList(false);
    fetchProductVariation();
  }
}, [refreshVariationList])

const addVariant = (type) => {
  setProductsForType([]);
  setPreventTriggerAPI(false);
  setAddVariantOpen(true);
  switch(type.toLowerCase()){
    case "color":
      setAddType("Color");
      break;
    case "weight":
      setAddType("Weight");
      break;
    default:
      setAddType("Size");
      break;
  }
}

useEffect(() => {
  const fetchProducts = async() => {
    setIsProductsForTypeFetchingDone(false);
    const productsInfo = await getAllAvailableProductsForVariation(animalId, categoryId, addType.toLowerCase());
    if(productsInfo.isSuccess){
      if(productsInfo.successResponse.data.data.length > 0){
        productsInfo.successResponse.data.data = productsInfo.successResponse.data.data.filter((product) => {
          if(product.id != productId){
            return product;
          }
        })
      }
      setProductsForType(productsInfo.successResponse.data.data);
      setIsProductsForTypeFetchingDone(true);
    }else{
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
    }
  }
  if(addType.trim() != "" && !preventTriggerAPI){
    fetchProducts();
  }
},[addType, preventTriggerAPI]);

const customStyles = {
  menu: (provided, state) => ({
    ...provided,
    maxHeight: "300px",
    overflow: "hidden"
  }),
  control: (provided, state) => ({
    ...provided,
    cursor: 'pointer'
  }),
  option: (provided, state) => ({
      ...provided,
      cursor: 'pointer'
    })
};
    const handleOnProductChange = (selectedOption) => {
      setSelectedProductForType(selectedOption);
    }

    const goBackVariantMenu = () => {
      if(addVariantOpen){
        setPreventTriggerAPI(true);
        setAddType("Size");
        setProductsForType([]);
        setIsProductsForTypeFetchingDone(true);
        setSelectedProductForType({});
        setAddVariantOpen(false);
      }
      if(productDetailsView){
        setCurrentProductForView({});
        setProductDetailsView(false);
      }
    }
  const createVariation = async () => {
    const variationAddedInfo = await createProductVariation(animalId, categoryId, productId, animalId, categoryId, selectedProductForType.id, addType.toLowerCase());
    if (variationAddedInfo.isSuccess) {
      setRefreshVariationList(true);
      goBackVariantMenu();
    } else {
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
    }
  }
  const onView = (product, variantType) => {
    setCurrentProductForView(product);
    setProductDetailsView(true);
  }
  const onRemove = async(isCurrentProductRemoval, product, variantType) => {
    const variationAddedInfo = isCurrentProductRemoval ? await deleteProductVariation(animalId, categoryId, productId, variantType.toLowerCase()) : await deleteProductVariation(animalId, categoryId, product.id, variantType.toLowerCase());
    if (variationAddedInfo.isSuccess) {
      setRefreshVariationList(true);
    } else {
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
    }
  }
    return(
      <div>
        <div className="flex flex-row "> 
        {(addVariantOpen || productDetailsView) && <ArrowLeftIcon className="w-6 h-6 cursor-pointer flex justify-center items-center" onClick={goBackVariantMenu} />}
        <h1 className={`text-xl font-bold ${(addVariantOpen || productDetailsView) ? "ml-2" : ""} flex justify-center items-center -mt-1`}>{addVariantOpen ? `Add ${addType} Variant` : productDetailsView ? "Product Details" : "Variation Details"}</h1>
        </div>
          {(!addVariantOpen && !productDetailsView) && 
          <div>
          <div className="mt-4 border-t border-black mb-5"></div>
           <VariationProducts variantProducts={bySizeProducts} keyItem="Size" addVariant={() => {addVariant("size");}} keyItemArrayId="size" maxVariantProducts={5} isVariantDetailsPresentInProduct={isSizeDataPresent} onView={onView} onRemove={onRemove}  />
           <div className="mt-5 border-b border-black mb-5"></div>
           <VariationProducts variantProducts={byWeightProducts} keyItem="Weight" addVariant={() => {addVariant("weight");}} keyItemArrayId="weight" maxVariantProducts={5} isVariantDetailsPresentInProduct={isWeightDataPresent} onView={onView} onRemove={onRemove}  />
           <div className="mt-5 border-b border-black mb-5"></div>
           <VariationProducts variantProducts={byColorProducts} keyItem="Color" addVariant={() => {addVariant("color");}} keyItemArrayId="color" maxVariantProducts={5} isVariantDetailsPresentInProduct={isColorDataPresent} onView={onView} onRemove={onRemove}  />
           <div className="mt-5 mb-5"></div>
      
        </div>
          }
          {
            addVariantOpen &&
            <div className="min-height-400px">
        <span className="block font-bold mt-2 mb-2" htmlFor="product">
          Select a Product
        </span>
        <div className="flex flex-row">
        <div className="flex-grow">
        <Select options={productsForType}  isLoading={!isProductsForTypeFetchingDone} styles={customStyles}
      placeholder={!isProductsForTypeFetchingDone ? 'Loading Products...' : 'Select a product'} onChange={handleOnProductChange} getOptionLabel={(option) => option.name}  getOptionValue={(option) => option.id} value={productsForType.find((c) => c.id === selectedProductForType.id)} />
        </div>
        <div className="ml-2 flex items-center justify-center">
        <button className="text-green-500 disabled:opacity-25 disabled:cursor-not-allowed" disabled={Object.keys(selectedProductForType).length <= 0} onClick={createVariation}>Save</button>
        </div>
        </div>
        {Object.keys(selectedProductForType).length > 0 && <Product />}
        </div>
          }
          {productDetailsView && 
          <div>
            {Object.keys(currentProductForView).length > 0 && <Product />}
          </div>
          }
        
      </div>
    );
}

export default Variation;