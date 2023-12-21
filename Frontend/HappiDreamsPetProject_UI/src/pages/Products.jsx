import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { deleteProduct, getAnimal, getCategoryBasedOnId, getProducts } from '../services/ApiClient';
import UINotification from '../components/UINotification';
import Pagination from '../components/Pagination';
import Select from "react-select";
import Modal from 'react-responsive-modal';
import Variation from '../components/Variation';
function Products({hideSortVisibility, maxCheckedForCheckBox, category_info_from_components, animal_info_from_components, hideTitleVisibility, handleCheckBox, checkedBoxIds, preventProductNavigation, isAdminPanelUsage, onEdit}){
    let { category_id } = useParams();
    let { animal_id } = useParams();
    const [categoryInfo, setCategoryInfo] = useState(category_info_from_components);
    const [animalInfo, setAnimalInfo] = useState(animal_info_from_components);
    const isAdminPanel = isAdminPanelUsage !== undefined ? isAdminPanelUsage : false;
    const [isAllDataInArrayUndefined, setIsAllDataInArrayUndefined] = useState(true);
    const [renderedElements, setRenderedElements] = useState();
    const [lowestPriceFilter, setLowestPriceFilter] = useState(-1);
    const [highestPriceFilter, setHighestPriceFilter] = useState(-1);
    const [isChecked, setIsChecked] = useState([]);
    const [products, setProducts] = useState([]);
    let apiPerPageLimit = isAdminPanel ? 32 : 30;
    const perPageProducts = isAdminPanel ? 16 : 15;
    const [apiPage, setApiPage] = useState(-1);
    const [incrementalApiPage, setIncrementalApiPage] = useState(-1);
    const [apiOngoing, setApiOngoing] = useState(false);
    const [totalProducts, setTotalProducts] = useState(products.length);
    const [totalPages, setTotalPages] = useState(Math.floor(totalProducts / perPageProducts) + (totalProducts % perPageProducts == 0 ? 0 : 1));
    const [currentProducts, setCurrentProducts] = useState([]);
    const [hasMoreElements, setHasMoreElements] = useState(false);  
    const [manualPageNumber, setManualPageNumber] = useState(-1);
    const [manualRefresh, setManualRefresh] = useState(false);
    const [currentPageNumber, setCurrentPageNumber] = useState(1);
    const [paginationIndex, setPaginationIndex] = useState(1);
    const [selectedSortValue, setSelectedSortValue] = useState({id: -1, value: "default", label: "Select an option"});
    const [userAction, setUserAction] = useState(true);
    const [applyPriceAction, setApplyPriceAction] = useState(false);
    const [applyPriceActionIndex, setApplyPriceActionIndex] = useState(0);
    const [variationModalIsOpen, setVariationModalIsOpen] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState({});
    const [sortValueOptions, setSortValuesOptions] = useState([
      {id: -1, value: "default", label: "Select an option"},
      {id: 1, value: "name_asc", label: "Name, ASC"},
      {id: 2, value: "name_desc", label: "Name, DESC"},
      {id: 3, value: "price_asc", label: "Price, ASC"},
      {id: 4, value: "price_desc", label: "Price, DESC"}
    ]);
    const customStyles = {
      menu: (provided, state) => ({
        ...provided,
        marginTop: "4px",
        borderRadius: "0.25rem",
        borderColor: "rgb(209, 213, 219)",
        fontSize: "0.875rem",
        lineHeight: "1.25rem",
        maxHeight: "200px",
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

    const navigate = useNavigate();

    useEffect(() => {
      const fetchAnimalInfo = async(animalId) => {
        const animalData = await getAnimal(animalId);
        if(animalData.isSuccess){
          setAnimalInfo(animalData.successResponse.data);
        }else{
          setAnimalInfo({id: -1, name: ""});
          UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
          navigate("/home");
        }
      }
      if(animal_info_from_components === undefined && animal_id){
        fetchAnimalInfo(animal_id);
      }
    }, [animal_id, animal_info_from_components]);

    useEffect(() => {
      const fetchCategoryInfo = async(categoryId) => {
        const categoryData = await getCategoryBasedOnId(animal_id, categoryId);
        if(categoryData.isSuccess){
          setCategoryInfo(categoryData.successResponse.data);
        }else{
          setCategoryInfo({id: -1, name: ""});
          UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
          navigate("/home");
        }
      }
      if(category_info_from_components === undefined && category_id && animal_id){
        fetchCategoryInfo(category_id);
      }
    }, [category_id, category_info_from_components]);

  const onPageNumberChange = (pageNumber, setFromItem, setToItem, setTotalPage, setTotalData) => {
    setCurrentPageNumber(pageNumber);
    setFromItem((pageNumber - 1) * perPageProducts + 1);
    setToItem(perPageProducts * pageNumber < totalProducts ? perPageProducts * pageNumber : totalProducts);
    setTotalPage(totalPages);
    setTotalData(totalProducts);
    return true;
  }

  const fetchProductsByConditions = async (sortCol, sortBy, priceMin, priceMax, hardRefresh) => {
    setApiOngoing(true);
    const productData = await getProducts(animalInfo.id, categoryInfo.id, hardRefresh ? 1 : apiPage, apiPerPageLimit,
      sortCol, sortBy, priceMin, priceMax);
    if (productData.isSuccess) {
      let oldData = products.slice();
      let newData = [...oldData, ...productData.successResponse.data.data];
      if(hardRefresh){
        newData = [...productData.successResponse.data.data];
      }
      setProducts(newData);
      if(hardRefresh){
        setCurrentPageNumber(1);
        setApiPage(1);
      }
      let infoData = productData.successResponse.data.info;
      setHasMoreElements(infoData.more_records);
      setTotalProducts(infoData.total_records);
      setApiOngoing(false);
    } else {
      setProducts({ id: -1, name: "" });
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
      navigate("/home");
    }
  }

  useEffect(() => {
    setTotalPages(Math.floor(totalProducts / perPageProducts) + (totalProducts % perPageProducts == 0 ? 0 : 1));
    setPaginationIndex(paginationIndex + 1);
  }, [totalProducts])

  useEffect(() => {
    if (categoryInfo && animalInfo) {
      setApiPage(1);
    }
  }, [categoryInfo, animalInfo]);

  useEffect(() => {
    const getProductsBasedOnSort = async() => {
      if(selectedSortValue.value !== "default"){
        let colName = selectedSortValue.value.split("_")[0];
        let sortOrder = selectedSortValue.value.split("_")[1];
        await fetchProductsByConditions(colName, sortOrder === "asc" ? true : sortOrder === "desc" ? false : undefined, lowestPriceFilter >= 0 ? lowestPriceFilter : undefined, highestPriceFilter >= 0 ? highestPriceFilter : undefined, (!userAction || applyPriceAction));
      }else{
        await fetchProductsByConditions(undefined, undefined, lowestPriceFilter >= 0 ? lowestPriceFilter : undefined, highestPriceFilter >= 0 ? highestPriceFilter : undefined, (!userAction || applyPriceAction));
      }
      if(!userAction){
        setUserAction(true);
      }    
      if(applyPriceAction){
        setApplyPriceAction(false);
      }
    }

    if (categoryInfo && animalInfo && apiPage > 0) {
      getProductsBasedOnSort();
    } 
  }, [selectedSortValue, apiPage, applyPriceActionIndex]);
  
  useEffect(() => {
    if(incrementalApiPage > 0){
      if(!(apiPage === incrementalApiPage) || (apiPage < incrementalApiPage)){
        if(!apiOngoing && hasMoreElements){
          setApiPage(apiPage + 1);
        }
      }else{
        setIncrementalApiPage(-1);
      }
    }
  },[incrementalApiPage, apiOngoing, hasMoreElements])

  useEffect(() => {
    let nextPageData = products.slice((currentPageNumber - 1) * perPageProducts, perPageProducts * currentPageNumber);
    if ((nextPageData.length === 0 || (nextPageData.length % perPageProducts !== 0)) && hasMoreElements) {
      setIncrementalApiPage(Math.floor(currentPageNumber / 2) + Math.floor(currentPageNumber % 2) );
    } else {
      renderElements(products.slice((currentPageNumber - 1) * perPageProducts, perPageProducts * currentPageNumber));
    }
  }, [currentPageNumber])

  useEffect(() => {
    if (manualRefresh && manualPageNumber > 0) {
      let nextPageData = products.slice((manualPageNumber - 1) * perPageProducts, perPageProducts * manualPageNumber);
      if ((nextPageData.length === 0 || (nextPageData.length % perPageProducts !== 0)) && hasMoreElements) {
        setApiPage(apiPage + 1);
      } else {
        renderElements(products.slice((currentPageNumber - 1) * perPageProducts, perPageProducts * currentPageNumber));
      }
    }
    setManualRefresh(false);
  }, [manualRefresh]);

  useEffect(() => {
    renderElements(products.slice((currentPageNumber - 1) * perPageProducts, perPageProducts * currentPageNumber));
    setTotalPages(Math.floor(totalProducts / perPageProducts) + (totalProducts % perPageProducts == 0 ? 0 : 1));
    if (currentPageNumber === 1) {
      setPaginationIndex(paginationIndex + 1);
    }
  }, [products]);

    const handleCheckBoxChange = (index, product) => {
      isChecked[index] = isChecked[index] !== undefined ? !isChecked[index] : true;
      const newChecked = [...isChecked];
      setIsChecked(newChecked);
      handleCheckBox(product, isChecked[index]);
    }

    const handleChange=(event)=> {
        let value = event.target.value;
        if (!(/^[0-9]*$/.test(value))) {
          return;
        }
        if(event.target.id === "FilterPriceFrom"){
          setLowestPriceFilter(Number(value));
        }else if(event.target.id === "FilterPriceTo"){
          setHighestPriceFilter(Number(value));
        }
    };
  
    const handleKeyPress = (event) => {
      const keyCode = event.keyCode || event.which;
      const keyValue = String.fromCharCode(keyCode);
      if(keyValue === '\b'){
        return;
      }
      if (/[^0-9]/.test(keyValue) ) {
        event.preventDefault();
      }
    };

    const applyPriceFilter = () => {
      setApplyPriceAction(true);
      setApplyPriceActionIndex( applyPriceActionIndex + 1 );
    }

    const resetPriceFilter = () => {
      setLowestPriceFilter(-1);
      setHighestPriceFilter(-1);
      setApplyPriceAction(false);
      setApplyPriceActionIndex( applyPriceActionIndex + 1 );
    }

    const handleInputBlur = (event) => {
        if(event.target.id === "FilterPriceFrom" && lowestPriceFilter != ""){
          if(event.target.value > highestPriceFilter && highestPriceFilter != "" && highestPriceFilter >= 0){
            event.target.value = highestPriceFilter;
            setLowestPriceFilter(event.target.value);
          }
        }else if(event.target.id === "FilterPriceTo" && highestPriceFilter != ""){
          if(event.target.value < lowestPriceFilter && lowestPriceFilter != "" && lowestPriceFilter >= 0){
            event.target.value = lowestPriceFilter;
            setHighestPriceFilter(event.target.value);
          }
        }
    }

    const renderProductColorElement = (element) => {
      if(element.hasOwnProperty("color") && element.color !== undefined){
        return(
          <span className='mr-2'>
            Color: {element.color}
          </span>
        )
      }
    }

    const renderProductSizeElement = (element) => {
      if(element.hasOwnProperty("size") && element.size !== undefined){
        return(
          <span className='mr-2'>
            Size: {element.size}
          </span>
        )
      }
    }

    const renderProductWeightElement = (element) => {
      if(element.hasOwnProperty("weight") && element.weight !== undefined){
        return(
          <span className='mr-2'>
            Weight: {element.weight} {element.weight_units}
          </span>
        )
      }
    }
      const onProductClick = (product) => {
        console.log(product);
        navigate("/" + animalInfo.id + "/" + categoryInfo.id + "/" + product.id)
      }

      const handleDelete = async (index, product) => {
        let data = [...products];
        index = ((currentPageNumber - 1) * perPageProducts) + index;
        const deleteResponse = await deleteProduct(animal_info_from_components.id, category_info_from_components.id, product.id);
        if(deleteResponse.isSuccess){
          data.splice(index, 1);
          setProducts(data);
          setTotalProducts(totalProducts - 1);
          setManualPageNumber(currentPageNumber);
          setManualRefresh(true);
          UINotification({ message: "Product " + product.name + " is deleted", type: "Success" });
        }else{
          UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
        }
      }

      const handleEdit = (productInfo) => {
        productInfo.isExternalUpload = true;
        productInfo.images = [];
        if(isAdminPanel){
          onEdit(productInfo);
        }
      }

      const handleVariation = (productInfo) => {
        setSelectedProduct(productInfo);
        setVariationModalIsOpen(true);
      }

      const handleSortChange = (value) => {
        setSelectedSortValue(value);
        setUserAction(false);
        setApiPage(1);
      }
    
      const renderElements = (productsData) => {
        setCurrentProducts(productsData);
      let isAllDataInArrayUndefinedLocal = isAllDataInArrayUndefined;
      setRenderedElements(productsData.map((element, index) => {
        if(isAllDataInArrayUndefined){
          setIsAllDataInArrayUndefined(false);
          isAllDataInArrayUndefinedLocal = false;
        }
        if(checkedBoxIds !== undefined && Array.isArray(checkedBoxIds) && checkedBoxIds.length > 0 && checkedBoxIds.includes(element.id)){
          isChecked[index] = true;
          setIsChecked(isChecked);
        }
          return(
            <li key={element.id}>
            <span className={`group block overflow-hidden ${preventProductNavigation !== undefined ? preventProductNavigation ? "" : "cursor-pointer" : "cursor-pointer"}`} onClick={preventProductNavigation !== undefined ? preventProductNavigation ? null : () => {onProductClick(element)} : () => {onProductClick(element)}}>
            
            <img
              src={element.thumbnailImageUrl ? element.thumbnailImageUrl : "https://dummyimage.com/350x350"}
              className="h-[350px] w-full object-cover transition duration-500 group-hover:scale-105 sm:h-[450px]"
            />

              <div className="relative bg-white pt-3">
                {handleCheckBox !== undefined && typeof handleCheckBox === 'function' &&
                <input
                type="checkbox"
                checked={isChecked[index]}
                onChange={() => {handleCheckBoxChange(index, element)}}
                className="form-checkbox h-5 w-5 text-indigo-600"
                disabled={maxCheckedForCheckBox !== undefined && maxCheckedForCheckBox !== null && ((isChecked[index] !== undefined && !isChecked[index]) || isChecked[index] === undefined)  && isChecked.filter((c) => c).length >= maxCheckedForCheckBox}
              />
                }
                <h3
                  className={`text-xs text-gray-700 ${isAdminPanel ? "" : "group-hover:underline group-hover:underline-offset-4"}`}
                >
                  {element.name}
                </h3>

                {isAdminPanel &&
                  <span className='flex flex-col text-xs italic font-bold'>
                    {renderProductColorElement(element)}
                    {renderProductSizeElement(element)}
                    {renderProductWeightElement(element)}
                
              </span>
              }
                

                <p className="mt-2">
                  <span className="sr-only"> Regular Price </span>
                  {!isAdminPanel ? 
                    <span className="tracking-wider text-gray-900">  {element.price} </span>
                    :
                  <div className='flex flex-row whitespace-nowrap'>
                  <span className="tracking-wider text-gray-900">  {element.price} </span>
                  <span className="ml-2 cursor-pointer mr-2 text-green-400 hover:text-green-500" onClick={() => {handleVariation(element)}}>Add a Variation</span>
                  <span className="cursor-pointer mr-2 text-indigo-500 hover:text-indigo-900" onClick={() => {handleEdit(element)}}>Edit</span>
                  <span className="cursor-pointer mr-2 text-red-500 hover:text-red-900" onClick={()=>{handleDelete(index, element)}}>Delete</span>
                  </div>
                }
                  
                </p>
              </div>
            </span>
          </li>
              )
      }
    ))
    
    if(isAllDataInArrayUndefinedLocal){
      setRenderedElements(<div>No Products Found</div>);
    }
    }

  return (
    <section>
    <div className="mx-auto max-w-screen-xl px-4 py-8 sm:px-6 sm:py-12 lg:px-8">
      <header className={`${hideTitleVisibility !== undefined ? hideTitleVisibility ? "hidden" : "" : ""}`}>
        <h2 className="text-xl font-bold text-gray-900 sm:text-3xl flex items-center justify-center mb-16">
          {animalInfo && animalInfo.hasOwnProperty("name") ? animalInfo.name : ""} - {categoryInfo && categoryInfo.hasOwnProperty("name") ? categoryInfo.name : ""} Collections
        </h2>
      </header>
  
    <div className={` ${hideSortVisibility !== undefined ? hideSortVisibility ? "hidden" : "" : ""}`}>
      <div className={`mt-8 block lg:hidden`}>
        <button
          className="flex cursor-pointer items-center gap-2 border-b border-gray-400 pb-1 text-gray-900 transition hover:border-gray-600"
        >
          <span className="text-sm font-medium"> Filters & Sorting </span>
  
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            strokeWidth="1.5"
            stroke="currentColor"
            className="h-4 w-4 rtl:rotate-180"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M8.25 4.5l7.5 7.5-7.5 7.5"
            />
          </svg>
        </button>
      </div>
      </div>


      <div className={` ${hideSortVisibility !== undefined ? hideSortVisibility ? "" : "mt-4 lg:mt-8 " : "mt-4 lg:mt-8 "} lg:grid lg:grid-cols-4 lg:items-start lg:gap-8`}>
      <div className={` ${hideSortVisibility !== undefined ? hideSortVisibility ? "hidden" : "" : ""}`}>
        <div className={`hidden space-y-4 lg:block`}>
          <div>
            <label className="block text-xs font-medium text-gray-700">
              Sort By
            </label>
    
            <Select options={sortValueOptions} styles={customStyles} onChange={handleSortChange}  getOptionValue={(option) => option.label} value={sortValueOptions.find((c) => c.value === selectedSortValue.value)} />
          </div>
  
          <div>
            <p className="block text-xs font-medium text-gray-700">Filters</p>
  
            <div className="mt-1 space-y-2">

  
              <details
                className="overflow-hidden rounded border border-gray-300 [&_summary::-webkit-details-marker]:hidden"
              >
                <summary
                  className="flex cursor-pointer items-center justify-between gap-2 p-4 text-gray-900 transition"
                >
                  <span className="text-sm font-medium"> Price </span>
  
                  <span className="transition group-open:-rotate-180">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      strokeWidth="1.5"
                      stroke="currentColor"
                      className="h-4 w-4"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M19.5 8.25l-7.5 7.5-7.5-7.5"
                      />
                    </svg>
                  </span>
                </summary>
  
                <div className="border-t border-gray-200 bg-white">
                  <header className="flex items-center justify-between p-4">
                    <span className={`text-sm text-gray-700`}>

                      {lowestPriceFilter > 0 || highestPriceFilter > 0 ? "" : "No Price Filter Selected"}
                    </span>
                    <button
                      type="button"
                      className="text-sm text-gray-900 underline underline-offset-4 float-right"
                      onClick={applyPriceFilter}
                    >
                      Apply
                    </button>
                    <button
                      type="button"
                      className="text-sm text-gray-900 underline underline-offset-4 float-right"
                      onClick={resetPriceFilter}
                    >
                      Reset
                    </button>
                  </header>
  
                  <div className="border-t border-gray-200 p-4">
                    <div className="flex justify-between gap-4">
                      <label
                        className="flex items-center gap-2"
                      >
                        <span className="text-sm text-gray-600">$</span>
  
                        <input
                          type="text"
                          id="FilterPriceFrom"
                          placeholder="From"
                          value={lowestPriceFilter > 0 ? lowestPriceFilter : ""}
                          onChange={handleChange}
                          onKeyDown={handleKeyPress}
                          onBlur={handleInputBlur}
                          className="w-full sm:text-sm"
                        />
                      </label>
  
                      <label className="flex items-center gap-2">
                        <span className="text-sm text-gray-600">$</span>
  
                        <input
                          type="text"
                          id="FilterPriceTo"
                          placeholder="To"
                          value={highestPriceFilter > 0 ? highestPriceFilter : ""}
                          onChange={handleChange}
                          onKeyDown={handleKeyPress}
                          onBlur={handleInputBlur}
                          className="w-full sm:text-sm"
                        />
                      </label>
                    </div>
                  </div>
                </div>
              </details>

            </div>
          </div>
        </div>
        </div>
  
        <div className={` ${hideSortVisibility !== undefined ? hideSortVisibility ? "lg:col-span-4" : "lg:col-span-3" : "lg:col-span-3"} `}>
          <ul className={`${isAllDataInArrayUndefined ? "flex items-center justify-center" : `grid gap-4 sm:grid-cols-2 ${hideSortVisibility !== undefined ? hideSortVisibility ? "lg:grid-cols-4" : "lg:grid-cols-3" : "lg:grid-cols-3"} `} `}>
            {renderedElements}
          </ul>
        </div>
      </div>
      {!isAllDataInArrayUndefined && 
        <>
         <div className="mt-10">
          <Pagination key={paginationIndex} totalPages={totalPages} onClickOfPageNumber={(pageNumber, setFromItem, setToItem, setTotalPage, setTotalData) => onPageNumberChange(pageNumber, setFromItem, setToItem, setTotalPage, setTotalData)} initialPerPageResult={currentProducts.length >= perPageProducts ? perPageProducts : currentProducts.length} totalResult={totalProducts} manualPageNumber={manualPageNumber > 0 ? manualPageNumber : undefined} />
        </div>
        </>}
        {variationModalIsOpen && (
        <Modal closeOnOverlayClick={false} open={variationModalIsOpen} onClose={() => setVariationModalIsOpen(false)} center blockScroll={true} closeIconId={"variationModalClose"}  styles={{modal: {width: '60%'}}}>
      <div onClick={(event) => event.stopPropagation()}>
        {/* <h1 className="text-xl font-bold mb-4 -mt-2">Variation Details</h1> */}
          <div><Variation closeModal={() => {event.stopPropagation()}} animalId={animalInfo.id} categoryId={categoryInfo.id} productId={selectedProduct.id} productName={selectedProduct.name} isSizeDataPresent={selectedProduct.hasOwnProperty("size")} isColorDataPresent={selectedProduct.hasOwnProperty("color")} isWeightDataPresent={selectedProduct.hasOwnProperty("weightUnits") && selectedProduct.hasOwnProperty("weight")} /> </div>
      </div>
      </Modal>
      )}
    </div>
  </section>
  );
  }
  export default Products;