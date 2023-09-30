import { useEffect, useState } from "react";
import VariationProducts from "./VariationProducts";
import Modal from "react-responsive-modal";
import { ArrowLeftIcon } from "@heroicons/react/24/solid";
import Product from "../pages/Product";
import Select from "react-select";
import { getAllAvailableProductsForVariation, getProductVariation } from "../services/ApiClient";
import UINotification from "./UINotification";

function Variation({animalId, categoryId, productId, productName}){
    const [bySizeProducts, setBySizeProducts] = useState([]);
    const [byColorProducts, setByColorProducts] = useState([]);
    const [byWeightProducts, setByWeightProducts] = useState([]);
    const [addVariantOpen, setAddVariantOpen] = useState(false);
    const [addType, setAddType] = useState("");
    const [productsForType, setProductsForType] = useState([]);
    const [isProductsForTypeFetchingDone, setIsProductsForTypeFetchingDone] = useState(true);
    const [selectedProductForType, setSelectedProductForType] = useState({});

const fetchProductVariation = async () => {
  setIsProductsForTypeFetchingDone(false);
    const variationInfo = await getProductVariation(animalId, categoryId, productId);
    debugger;
    if(variationInfo.isSuccess){
      if(variationInfo.successResponse.data.data.length == 0){
        setBySizeProducts([]);
        setByColorProducts([]);
        setByWeightProducts([]);
      }else{
        if(variationInfo.successResponse.data.data)
      }
    }else{
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
    }
}

useEffect(() => {
  fetchProductVariation();
}, [])

const addVariant = (type) => {
  setProductsForType([]);
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
  setProductsForType([{id: 1, name: "Dummy Product 1", color: "Red"}]);
}

useEffect(() => {
  const fetchProducts = async() => {
    setIsProductsForTypeFetchingDone(false);
    const productsInfo = await getAllAvailableProductsForVariation(animalId, categoryId, addType.toLowerCase());
    if(productsInfo.isSuccess){
      setProductsForType(productsInfo.successResponse.data.data);
      setIsProductsForTypeFetchingDone(true);
    }else{
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
    }
  }
  if(addType.trim() != ""){
    fetchProducts();
  }
},[addType]);

const customStyles = {
  menu: (provided, state) => ({
    ...provided,
    maxHeight: "150px",
    overflow: "hidden",
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
        setAddType("Size");
        setProductsForType([]);
        setIsProductsForTypeFetchingDone(true);
        setSelectedProductForType({});
        setAddVariantOpen(false);
      }
    }
    return(
      <div>
        <div className="flex flex-row "> 
        {addVariantOpen && <ArrowLeftIcon className="w-6 h-6 cursor-pointer flex justify-center items-center" onClick={goBackVariantMenu} />}
        <h1 className={`text-xl font-bold ${addVariantOpen ? "ml-2" : ""} flex justify-center items-center -mt-1`}>{addVariantOpen ? `Add ${addType} Variant` : "Variation Details"}</h1>
        </div>
          {!addVariantOpen ? 
          <div>
          <div className="mt-4 border-t border-black mb-5"></div>
           <VariationProducts variantProducts={bySizeProducts} keyItem="Size" addVariant={() => {addVariant("size");}} keyItemArrayId="size" maxVariantProducts={5}  />
           <div className="mt-5 border-b border-black mb-5"></div>
           <VariationProducts variantProducts={byWeightProducts} keyItem="Weight" addVariant={() => {addVariant("weight");}} keyItemArrayId="weight" maxVariantProducts={5}  />
           <div className="mt-5 border-b border-black mb-5"></div>
           <VariationProducts variantProducts={byColorProducts} keyItem="Color" addVariant={() => {addVariant("color");}} keyItemArrayId="color" maxVariantProducts={5}  />
           <div className="mt-5 mb-5"></div>
      
        </div> : 
        <>
        <span className="block font-bold mt-2 mb-2" htmlFor="product">
          Select a Product
        </span>
        <div className="flex flex-row">
        <div className="flex-grow">
        <Select options={productsForType}  isLoading={!isProductsForTypeFetchingDone} styles={customStyles}
      placeholder={!isProductsForTypeFetchingDone ? 'Loading Products...' : 'Select a product'} onChange={handleOnProductChange} getOptionLabel={(option) => option.name}  getOptionValue={(option) => option.id} value={productsForType.find((c) => c.id === selectedProductForType.id)} />
        </div>
        <div className="ml-2 flex items-center justify-center">
        <button className="text-green-500 disabled:opacity-25 disabled:cursor-not-allowed" disabled={Object.keys(selectedProductForType).length <= 0}>Save</button>
        </div>
        </div>
        {Object.keys(selectedProductForType).length > 0 && <Product />}
        </>
          }
        
      </div>
    );
}

export default Variation;