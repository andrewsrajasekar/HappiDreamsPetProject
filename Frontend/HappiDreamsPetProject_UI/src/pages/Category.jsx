import { useEffect, useRef, useState } from "react";
import Pagination from "../components/Pagination";
import { useNavigate, useParams } from 'react-router-dom';
import { CATEGORY_TYPE } from "../utils/CategoryTypes";
import { deleteAnimal, deleteCategory, getAnimals, getCategory } from "../services/ApiClient";
import UINotification from "../components/UINotification";

function Category({ categoryType, isAdminPanelUsage, onDelete, onEdit, animalId_AdminPanel }) {
  
  let { animal_id } = useParams();
  const isAdminPanel = isAdminPanelUsage !== undefined ? isAdminPanelUsage : false;
  animal_id = isAdminPanel ? animalId_AdminPanel : animal_id;
  const isAnimalTypeCategory = (categoryType == CATEGORY_TYPE.ANIMAL_CATEGORY);
  const isAnimalProductTypeCategory = (categoryType == CATEGORY_TYPE.ANIMAL_PRODUCT_CATEGORY);
  const [manualPageNumber, setManualPageNumber] = useState(-1);
  const [manualRefresh, setManualRefresh] = useState(false);
  const [currentPageNumber, setCurrentPageNumber] = useState(1);
  const [paginationIndex, setPaginationIndex] = useState(1);
  const navigate = useNavigate();
  const perPageCategories = 6;
  const [animals, setAnimals] = useState([]);
  const [categories, setCategories] = useState([]);
  const [hasMoreElements, setHasMoreElements] = useState(false);
  let apiPerPageLimit = 12;
  const [apiPage, setApiPage] = useState(1);
  const [incrementalApiPage, setIncrementalApiPage] = useState(-1);

  const [elements, setElements] = useState();
  const [apiOngoing, setApiOngoing] = useState(false);
  const [totalCategories, setTotalCategories] = useState(isAnimalTypeCategory ? animals.length : categories.length);
  const [totalPages, setTotalPages] = useState(Math.floor(totalCategories / perPageCategories) + (totalCategories % perPageCategories == 0 ? 0 : 1));

  const divElement = useRef(null);

  const fetchAPIData = async () => {
    setApiOngoing(true);
    const response = isAnimalProductTypeCategory ? await getCategory(animal_id, apiPage, apiPerPageLimit) : await getAnimals(apiPage, apiPerPageLimit);
    if (response.isSuccess) {
      if (response.successResponse.data.hasOwnProperty("data") && Array.isArray(response.successResponse.data.data) && response.successResponse.data.data.length > 0) {
        let oldData = isAnimalProductTypeCategory ? categories.slice() : animals.slice();
        let newData = [...oldData, ...response.successResponse.data.data];
        isAnimalProductTypeCategory ? setCategories(newData) : setAnimals(newData);
        let infoData = response.successResponse.data.info;
        setHasMoreElements(infoData.more_records);
        setTotalCategories(infoData.total_records);
      } else {
        setHasMoreElements(false);
      }
      setApiOngoing(false);
    } else {
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
    }
  };

  useEffect(() => {
    fetchAPIData();
  }, []);

  const onPageNumberChange = (pageNumber, setFromItem, setToItem, setTotalPage, setTotalData) => {
    setCurrentPageNumber(pageNumber);
    setFromItem((pageNumber - 1) * perPageCategories + 1);
    setToItem(perPageCategories * pageNumber < totalCategories ? perPageCategories * pageNumber : totalCategories);
    setTotalPage(totalPages);
    setTotalData(totalCategories);
    return true;
  }

  useEffect(() => {
    fetchAPIData();
  }, [apiPage]);

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
    let nextPageData = isAnimalProductTypeCategory ? categories.slice((currentPageNumber - 1) * perPageCategories, perPageCategories * currentPageNumber) : animals.slice((currentPageNumber - 1) * perPageCategories, perPageCategories * currentPageNumber);
    if ((nextPageData.length === 0 || (nextPageData.length % perPageCategories !== 0)) && hasMoreElements) {
      setIncrementalApiPage(Math.floor(currentPageNumber / 2) + Math.floor(currentPageNumber % 2) );
    } else {
      if (isAnimalTypeCategory) {
        renderElements(animals.slice((currentPageNumber - 1) * perPageCategories, perPageCategories * currentPageNumber));
      } else {
        renderElements(categories.slice((currentPageNumber - 1) * perPageCategories, perPageCategories * currentPageNumber));
      }
    }
  }, [currentPageNumber])

  useEffect(() => {
    if (manualRefresh && manualPageNumber > 0) {
      let nextPageData = isAnimalProductTypeCategory ? categories.slice((manualPageNumber - 1) * perPageCategories, perPageCategories * manualPageNumber) : animals.slice((manualPageNumber - 1) * perPageCategories, perPageCategories * manualPageNumber);
      if ((nextPageData.length === 0 || (nextPageData.length % perPageCategories !== 0)) && hasMoreElements) {
        setApiPage(apiPage + 1);
      } else {
        if (isAnimalTypeCategory) {
          renderElements(animals.slice((currentPageNumber - 1) * perPageCategories, perPageCategories * currentPageNumber));
        } else {
          renderElements(categories.slice((currentPageNumber - 1) * perPageCategories, perPageCategories * currentPageNumber));
        }
      }
    }
    setManualRefresh(false);
  }, [manualRefresh])

  useEffect(() => {
    if (isAnimalTypeCategory) {
      renderElements(animals.slice((currentPageNumber - 1) * perPageCategories, perPageCategories * currentPageNumber));
    } else {
      renderElements(categories.slice((currentPageNumber - 1) * perPageCategories, perPageCategories * currentPageNumber));
    }
    setTotalPages(Math.floor(totalCategories / perPageCategories) + (totalCategories % perPageCategories == 0 ? 0 : 1));
    if (currentPageNumber === 1) {
      setPaginationIndex(paginationIndex + 1);
    }
  }, [animals, categories])

  const openProductsForCategories = (category) => {
    navigate("/" + animal_id + "/" + category["id"] + "/" + "products");
  }

  const openAnimalProductsForAnimal = (animal) => {
    navigate("/" + animal["id"] + "/" + "categories");
  }

  const handleDelete = async (index, currentElement) => {
    let data = [];
    index = ((currentPageNumber - 1) * perPageCategories) + index;
    if (isAnimalTypeCategory) {
      data = [...animals];
    } else {
      data = [...categories];
    }
    const deleteResponse = isAnimalTypeCategory ? await deleteAnimal(currentElement.id) : await deleteCategory(animal_id, currentElement.id);
    if(deleteResponse.isSuccess){
      data.splice(index, 1);
      if (isAnimalTypeCategory) {
        setAnimals(data);
      } else {
        setCategories(data);
      }
      setManualPageNumber(currentPageNumber);
      setManualRefresh(true);
      UINotification({ message: (isAnimalTypeCategory ? "Animal" : "Category") + currentElement.name + " is deleted", type: "Success" });
      if(onDelete && typeof onDelete === "function"){
        onDelete();
      }
    }else{
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
    }

  }

  const handleEdit = (data) => {
    data.isExternalUpload = data.image.imageType === "external_url" ? true : false;
    data.imageUrl = data.image.imageUrl;
    onEdit(data);
  }

  const renderElements = (categoriesData) => {
    let isCategoryPageAndElementsEmpty = isAnimalProductTypeCategory && categoriesData.length == 0;
    if (isCategoryPageAndElementsEmpty) {
      if (divElement.current) {
        divElement.current.classList.remove('flex');
      }
    } else {
      divElement.current.classList.add('flex');
    }
    setElements(
      <>
        {isCategoryPageAndElementsEmpty &&
          <>
            <div className="flex items-center justify-center text-base title-font font-normal mb-1 text-black">
              No Categories Found
            </div>
          </>
        }
        {!isCategoryPageAndElementsEmpty &&
          categoriesData.map((data, index) => {
            if(!data.hasOwnProperty("image")){
              data.image = "https://dummyimage.com/600x360";
            }  
            return (
              <div className={`lg:w-1/3 sm:w-1/2 p-4 ${isAdminPanel ? "" : "cursor-pointer"}`} key={data.id} onClick={isAdminPanel ? null : () => { isAnimalProductTypeCategory ? openProductsForCategories(data) : openAnimalProductsForAnimal(data) }}>
                <div className="flex relative">
                  <img alt="gallery" className="absolute inset-0 w-full h-full object-contain object-center placeholderColor" src={data.image.imageUrl} />
                  <div className="px-8 py-10 relative z-10 w-full border-4 border-gray-200 bg-white opacity-0 hover:opacity-100">
                    <h2 className="tracking-widest text-sm title-font font-medium text-indigo-500 mb-1">{data.name}</h2>
                    <p className="leading-relaxed">{data.description}</p>
                    {isAdminPanel &&
                      <div className="flex-row items-start justify-center mt-1">
                        <span className="mr-5 cursor-pointer text-indigo-500 hover:text-indigo-900" onClick={() => { handleEdit(data) }}>Edit</span>
                        <span className="cursor-pointer text-red-500 hover:text-red-900" onClick={() => { handleDelete(index, data) }}>Delete</span>
                      </div>
                    }
                  </div>
                </div>
              </div>
            )
          })
        }

      </>
    );
  }


  return (
    <section className="text-gray-600 body-font">
      <div className={`container ${isAdminPanel ? "px-5 py-5 mt-5" : "px-5 py-24"} mx-auto`}>
        {!isAdminPanel &&
          <div className="flex flex-col text-center w-full mb-12">
            <h1 className="sm:text-3xl text-2xl font-medium title-font text-gray-900">{isAnimalProductTypeCategory ? "Categories" : "Animal Types"}</h1>
          </div>
        }
        <div className="flex flex-wrap -m-4" ref={divElement}>
          {!apiOngoing && elements}
          {apiOngoing && 
          <>
             <svg className="bg-indigo-500 animate-spin h-5 w-5 mr-3 ..." viewBox="0 0 24 24">
              Loading Data
              </svg>
          </>
          }
        </div>
        <div className="mt-10">
          <Pagination key={paginationIndex} totalPages={totalPages} onClickOfPageNumber={(pageNumber, setFromItem, setToItem, setTotalPage, setTotalData) => onPageNumberChange(pageNumber, setFromItem, setToItem, setTotalPage, setTotalData)} initialPerPageResult={perPageCategories} totalResult={totalCategories} manualPageNumber={manualPageNumber > 0 ? manualPageNumber : undefined} />
        </div>

      </div>
    </section>
  );
}

export default Category;