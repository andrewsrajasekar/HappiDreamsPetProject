// Default theme
import '@splidejs/react-splide/css';

// or other themes
import '@splidejs/react-splide/css/skyblue';
import '@splidejs/react-splide/css/sea-green';

// or only core styles
import '@splidejs/react-splide/css/core';
import { useEffect, useState } from 'react';


function HomeCategorySampleProducts({categorySampleProducts}){
  const [currentCategorySampleProduct, setCurrentCategorySampleProduct] = useState(categorySampleProducts[0]);
  const [currentCategorySampleProductIndex, setCurrentCategorySampleProductIndex] = useState(0);

  let currentCategorySampleProductSize = 0;
  currentCategorySampleProduct["products"].map((element) => {
    currentCategorySampleProductSize++;
  });

  let categorySampleProductsSize = categorySampleProducts.length;
  let mdwidthClassName = "md:w-1/" + (currentCategorySampleProductSize > 4 ? 4 : currentCategorySampleProductSize); 
  //let paddingClassName = "p-" + currentCategorySampleProductSize; 

  const productsData = currentCategorySampleProduct["products"].map((element, index) => {
    return(
      <div className={`${mdwidthClassName} sm:mb-0 mb-6`} key={`category_sample_product_${index}`} id={`category_sample_product_${index}`} style={{flex: "0 0 calc(33.33% - 2rem)", margin: "4px"}}>
        <div className="rounded-lg h-64 overflow-hidden">
          <img alt="content" className="object-cover object-center h-full w-full" src={`${element["image_url"]}`} />
        </div>
        <h2 className="text-xl font-medium title-font text-gray-900 mt-5">{element["name"]}</h2>
        <p className="text-base leading-relaxed mt-2">{element["description"]}</p>
      </div>
    )
  });

  let smClassName = "sm:-m-" + currentCategorySampleProductSize;
  let mxClassName = "-mx-" + currentCategorySampleProductSize;
  let mtClassName = "-mt-" + currentCategorySampleProductSize;

const progressBarWidth = ((currentCategorySampleProductIndex + 1) / categorySampleProductsSize) * 100;

useEffect(() => {
  setCurrentCategorySampleProduct(categorySampleProducts[currentCategorySampleProductIndex]);
}, [currentCategorySampleProductIndex]);

const onNextCategoryClick = () => {
  if(currentCategorySampleProductIndex <  (currentCategorySampleProductSize - 1)){
    setCurrentCategorySampleProductIndex(currentCategorySampleProductIndex + 1);
  }
}

const onPrevCategoryClick = () => {
  if(currentCategorySampleProductIndex > 0){
    setCurrentCategorySampleProductIndex(currentCategorySampleProductIndex - 1);
  }
}

return(
<section className="text-gray-600 body-font">
  <div className="mx-auto px-5 py-14 mx-auto">
    <div className="flex flex-col">
      <div className="h-1 bg-gray-200 rounded overflow-hidden">
        <div className={`h-full bg-indigo-500`} style={{ width: `${progressBarWidth}%` }}></div>
      </div>
      <div className="flex flex-row justify-center items-center py-6 mb-10">
        <button disabled={currentCategorySampleProductIndex <= 0} className={`relative transition-all ease-in-out duration-300 w-10 h-10 bg-[#5A5CC9] text-white opacity-100 ${ ( currentCategorySampleProductIndex <= 0 ) ? "opacity-50 cursor-not-allowed" : ""}`} onClick={onPrevCategoryClick}><i className="bx bx-chevron-left text-2xl"></i></button>
        <span className="text-gray-900 font-medium title-font text-2xl mx-2.5">{currentCategorySampleProduct["animalType"]} - {currentCategorySampleProduct["categoryName"]}</span>
        <button disabled={currentCategorySampleProductIndex >= (currentCategorySampleProductSize - 1)} className={`relative transition-all ease-in-out duration-300 w-10 h-10 bg-[#5A5CC9] text-white opacity-100 ${ ( currentCategorySampleProductIndex >= (currentCategorySampleProductSize - 1) ) ? "opacity-50 cursor-not-allowed" : ""}`} onClick={onNextCategoryClick}><i className="bx bx-chevron-right text-2xl"></i></button>
        {/* <p className="sm:w-3/5 leading-relaxed text-base sm:pl-10 pl-0">Dummy Description</p> */}
        </div>
    </div>
    <div className={`flex flex-wrap ${smClassName} ${mxClassName} -mb-10 ${mtClassName}`}>
      {productsData}
    </div>
  </div>
</section>
);
}

export default HomeCategorySampleProducts;