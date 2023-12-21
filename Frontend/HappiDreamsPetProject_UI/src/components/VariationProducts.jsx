import { useEffect, useState } from "react";
import ReactTooltip from "react-tooltip";
function VariationProducts({variantProducts, keyItem, keyItemArrayId, maxVariantProducts, addVariant, isVariantDetailsPresentInProduct, onRemove, onView}){
  const [startIndex, setStartIndex] = useState(0);
const handleNext = () => {
  if ((startIndex + 2) < (variantProducts.length - 1)) {
    setStartIndex(startIndex + 1);
  }
}
const handlePrev = () => {
  if (startIndex > 0) {
    setStartIndex(startIndex - 1);
  }
}
useEffect(() => {
  if(variantProducts.length >= maxVariantProducts){
    ReactTooltip.rebuild();
  }
}, [variantProducts.length])
const visibleVariantProducts = variantProducts.slice(startIndex, startIndex + 3);
    return(
        <div className="bg-white overflow-x-hidden">
          <div>
          <div className={`${variantProducts.length > 0 ? "mb-4" : "mb-2"}`}>
            <span>{keyItem} Variants</span>
            {isVariantDetailsPresentInProduct ? 
            <>
            {variantProducts.length < maxVariantProducts  && 
                  <button className="ml-2 text-xs text-green-500" onClick={addVariant ? addVariant : null}>+Add</button>
              }
              {variantProducts.length >= maxVariantProducts  && 
              <>
               <span data-tip={`Only ${maxVariantProducts} Variant Products for ${keyItem} are allowed`} data-for="disabledButton" data-tip-disable={false}>
  
               <button className="ml-2 text-xs text-green-500 disabled:opacity-25 disabled:cursor-not-allowed" disabled>+Add</button>
  
               </span>
               <ReactTooltip id="disabledButton" place="bottom" effect="solid" />
              </>
                  
              }
            </>
                 :
              <>
              <span data-tip={`${keyItem} Details are not present in the Product, hence ${keyItem} Variant cannot be added`} data-for="addDisabledButton" data-tip-disable={false}>
  
              <button className="ml-2 text-xs text-green-500 disabled:opacity-25 disabled:cursor-not-allowed" disabled>+Add</button>

              </span>
              <ReactTooltip id="addDisabledButton" place="bottom" effect="solid" />
              </>
            }
            {variantProducts.length > 0 &&
                  <button className="ml-2 text-xs text-red-500" onClick={onRemove ? ()=>{onRemove(true, {}, keyItem)} : undefined}>Remove from Variation</button>
              }
        
            </div>
            <div className="">
          {variantProducts.length > 0 ? 
          <div className="flex justify-center">
            <div className="flex justify-evenly top left w-max h-full">
              <div className="flex items-center justify-center mr-1">
          <button
          onClick={handlePrev}
            className="hover:bg-blue-900/75 text-white w-10 h-full text-center opacity-75 hover:opacity-100 disabled:opacity-25 disabled:cursor-not-allowed z-10 p-0 m-0 transition-all ease-in-out duration-300"
          disabled={startIndex <= 0}
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-12 w-20 -ml-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              strokeWidth={2}
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M15 19l-7-7 7-7"
              />
            </svg>
            <span className="sr-only">Prev</span>
          </button>
          </div>
          <ul role="list" class="grid variantUl grid-cols-1 gap-x-6 gap-y-8 grid-cols-3 gap-x-8">
              {visibleVariantProducts.map((product) => (
                <li class="overflow-hidden rounded-xl border variantLi" id={`${keyItem}_variant_${product.id}`}>
                <div class="flex items-center justify-center border-b borderColorGrey bggrey">
                  <img src={product.thumbnailImageUrl ? product.thumbnailImageUrl : "https://dummyimage.com/350x350"} alt="Tuple" class="h-fit w-fit flex-none rounded-lg bg-white object-cover offsetShadow ring-gray-900/[.1]" />
                </div>
                <dl class="-my-3 variationBorderSeperate variationBorder px-4 py-4 text-sm leading-6">
                <div class="flex justify-between gap-x-4 py-3 variantDiv">
                    <dt class="text-gray-500 font-medium">Name</dt>
                    <dd class="text-gray-700">
                      <div className="text-sm leading-6 break-all flex">{product.name}</div>
                    </dd>
                  </div>
                  <div class="flex justify-between gap-x-4 py-3 variantDiv">
                    <dt class="text-gray-500 font-medium">{keyItem}</dt>
                    <dd class="text-gray-700">
                      <div className="text-sm leading-6 break-all flex">{product[keyItemArrayId]}</div>
                    </dd>
                  </div>
                  <div class="flex justify-center gap-x-4 py-3 variantDiv">
                    <dd><button className="text-xs text-indigo-500" onClick={onView ? ()=>{onView(product, keyItem)} : undefined}>View</button></dd>
                    <dd><button className="ml-2 text-xs text-red-500" onClick={onRemove ? ()=>{onRemove(false, product, keyItem)} : undefined}>Remove</button></dd>
                  </div>
                </dl>
              </li>
              ))}
      </ul>
      <div className="flex items-center justify-center ml-1 ">
          <button
            onClick={handleNext}
            className="hover:bg-blue-900/75 text-white w-10 h-full text-center opacity-75 hover:opacity-100 disabled:opacity-25 disabled:cursor-not-allowed z-10 p-0 m-0 transition-all ease-in-out duration-300"
            disabled={(startIndex + 2) >= (variantProducts.length - 1)}
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-12 w-20 -ml-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              strokeWidth={2}
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M9 5l7 7-7 7"
              />
            </svg>
            <span className="sr-only">Next</span>
          </button>
          </div>
        </div>
            
      </div>
       : 
       <>
       <div className="flex items-center justify-center text-gray-400">No {keyItem} Variants Found</div>
       </>}
       </div>
       </div>
        </div>
    );
}

export default VariationProducts;