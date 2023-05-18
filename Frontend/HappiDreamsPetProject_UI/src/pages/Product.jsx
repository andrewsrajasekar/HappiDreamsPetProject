import React, { useState } from "react";
import Carousel from "../components/Carousel";
import {CheckCircleIcon} from '@heroicons/react/20/solid';
import { useParams } from "react-router-dom";

function Product(){
  const { category_name } = useParams();
  const { animal_type } = useParams();
  const { product_id } = useParams();
    const product = {
      "animalType": "Dog",
      "category": "Dummy Category 1",
      "name": "Dummy Product",
      "description": "id cursus metus aliquam eleifend mi in nulla posuere sollicitudin aliquam ultrices sagittis orci a scelerisque purus semper eget duis at tellus at urna condimentum mattis pellentesque id nibh tortor id aliquet lectus proin nibh nisl condimentum id venenatis a condimentum vitae sapien pellentesque habitant morbi tristique senectus et netus",
      "details": 'Details:\n 1.Dummy point 1\n 2.Dummy Point 2 \n 3.Dummy Point 3',
      "colors": ["Red", "Blue", "Green"],
      "size": ["Small", "Medium", "Large"],
      "price": "58.00",
      "currency": "\u20B9",
      "images": [
      'https://dummyimage.com/400x400',
      'https://dummyimage.com/400x400',
      'https://dummyimage.com/400x400',
      'https://dummyimage.com/400x400'
      ],
      "stock": 10
    }

  const [isLoading, setIsLoading] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  const handleAddToCartClick = () => {
    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      setIsSuccess(true);
    }, 2000);
  }

    const currentTabClassName = "border-indigo-500 text-indigo-500";
    const [currentTab, setCurrentTab] = useState("description");

    const [currentColorIndex, setCurrentColorIndex] = useState(0);
    const [currentSizeIndex, setCurrentSizeIndex] = useState(0);
    const [quantity, setQuantity] = useState(1);

    const handleLeftArrowColorClick = () => {
      setCurrentColorIndex(currentColorIndex === 0 ? product["colors"].length - 1 : currentColorIndex - 1);
    };
  
    const handleRightArrowColorClick = () => {
      setCurrentColorIndex(currentColorIndex === product["colors"].length - 1 ? 0 : currentColorIndex + 1);
    };

    const handleLeftArrowSizeClick = () => {
      setCurrentSizeIndex(currentSizeIndex === 0 ? product["size"].length - 1 : currentSizeIndex - 1);
    };
  
    const handleRightArrowSizeClick = () => {
      setCurrentSizeIndex(currentSizeIndex === product["size"].length - 1 ? 0 : currentSizeIndex + 1);
    };

    const handleQuantityIncrease = () => {
      setQuantity(quantity + 1);
    };
  
    const handleQuantityDecrease = () => {
      if(quantity > 1){
        setQuantity(quantity - 1);
      } 
    };

    const handleTabChange = (tabName) => {
        setCurrentTab(tabName);
    }

return(
    <section className="text-gray-600 body-font overflow-hidden">
  <div className="container px-5 py-2 mx-auto">
    <div className="lg:w-4/5 mx-auto flex flex-wrap">
      <div className="lg:w-1/2 w-full lg:pr-10 lg:py-6 mb-6 lg:mb-0">
        <h2 className="text-sm title-font text-gray-500 tracking-widest">{product["animalType"]} - {product["category"]}</h2>
        <h1 className="text-gray-900 text-3xl title-font font-medium mb-4">{product["name"]}</h1>
      
        <div className="flex border-t border-gray-200 py-2">
          <span className="text-gray-500">Color</span>
          <span className="ml-auto text-gray-900">

          <div className="items-center justify-center">
          <div className="flex items-center space-x-4">
            <button onClick={handleLeftArrowColorClick} className="text-xl">&lt;</button>
            <div className="text-center">{product["colors"][currentColorIndex]}</div>
            <button onClick={handleRightArrowColorClick} className="text-xl">&gt;</button>
          </div>
        </div>
          </span>
        </div>
        <div className="flex border-t  border-gray-200 py-2">
          <span className="text-gray-500">Size</span>
          <span className="ml-auto text-gray-900">
          <div className="items-center justify-center">
          <div className="flex items-center space-x-4">
            <button onClick={handleLeftArrowSizeClick} className="text-xl">&lt;</button>
            <div className="text-center">{product["size"][currentSizeIndex]}</div>
            <button onClick={handleRightArrowSizeClick} className="text-xl">&gt;</button>
          </div>
        </div>

          </span>
        </div>
        <div className="flex border-t border-b mb-6 border-gray-200 py-2">
          <span className="text-gray-500">Quantity</span>
          <span className="ml-auto text-gray-900">
          <div className="flex items-center space-x-4">
      <button
        className={`px-3 py-2 text-black-700 text-sm rounded-md bg-purple-600 ${quantity <= 1 ? "" : 'hover:bg-purple-900 focus:outline-none focus:ring focus:ring-gray-300'} `}
        onClick={handleQuantityDecrease}
        disabled={quantity <= 1}
      >
        -
      </button>
      <span>{quantity}</span>
      <button
        className={`px-3 py-2 text-black-700 text-sm bg-purple-600 rounded-md ${quantity >= product['stock'] ? "" : 'hover:bg-purple-900 focus:outline-none focus:ring focus:ring-gray-300'}`}
        onClick={handleQuantityIncrease}
        disabled={quantity >= product["stock"]}
      >
        +
      </button>
    </div>

          </span>
        </div>
        <div className="flex">
          <span className="title-font font-medium text-2xl text-gray-900">{product["currency"]}{product["price"]}</span>
          <button className={`flex ml-auto text-white bg-purple-600 border-0 py-2 px-6 rounded items-center justify-center ${isLoading ? 'opacity-50 cursor-wait' :  isSuccess ? "bg-gray-300" : 'hover:bg-blue-600 focus:outline-none hover:bg-purple-900'}`} onClick={handleAddToCartClick} disabled={isLoading || isSuccess}>{isLoading ? (
       
       <svg className="animate-spin h-5 w-5 mr-3" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm16 0a8 8 0 01-8 8V24c10.627 0 16-5.373 16-12h-4zm-8-8V0H8a8 8 0 018 8z" />
        </svg>
      ) : isSuccess ? (
        <CheckCircleIcon className="inline-block mr-1 h-6 w-6 text-green-500" /> 
      ) : (
        'Add to Cart'
      )}
      {isLoading ? 'Adding to Cart...' : isSuccess ? 'Added to Cart' : ''}
      
      </button>
          {/* <button className="rounded-full w-10 h-10 bg-gray-200 p-0 border-0 inline-flex items-center justify-center text-gray-500 ml-4">
            <svg fill="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" className="w-5 h-5" viewBox="0 0 24 24">
              <path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 000-7.78z"></path>
            </svg>
          </button> */}
        </div>
      </div>
      <div className="lg:w-1/2 w-full">
     <Carousel data={product["images"]} className="lg:h-auto h-64 object-cover object-center rounded flex items-center"/>
        </div>
    </div>
    <div className="lg:w-4/5 mx-auto flex flex-wrap">
          <span className={`flex-grow  ${currentTab == "description" ? currentTabClassName : ""} border-b-2 py-2 text-lg px-1 cursor-pointer`} onClick={() => {handleTabChange("description")}}>Description</span>
          <span className={`flex-grow border-b-2 border-gray-300 ${currentTab == "details" ? currentTabClassName : ""} py-2 text-lg px-1 cursor-pointer`}  onClick={() => {handleTabChange("details")}}>Details</span>
        </div>
        <div className="lg:w-4/5 mx-auto flex">
        {currentTab == "description" && <p className="leading-relaxed mb-4 whitespace-pre-line items-center justify-center">{product["description"]}</p>}
        {currentTab == "details" && <p className="leading-relaxed mb-4 whitespace-pre-line items-center justify-center">{product["details"]}</p>}
        </div>

  </div>
</section>
);
}

export default Product;