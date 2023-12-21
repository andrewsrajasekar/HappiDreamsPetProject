import { useEffect, useState } from "react";
import HomeCategorySampleProducts from "../../../pages/HomeCategorySampleProducts";
import ReactTooltip from 'react-tooltip'
import Modal from "../../Modal";
import HomeCategoryAddPopUp from "./HomeCategoryAddPopUp";
import Sorter from "../Sorter";
import { addTopCategory, deleteTopCategory, getTopCategories, getTopCategory } from "../../../services/ApiClient";
import UINotification from "../../UINotification";

function HomeCategory(){

    const [categorySampleProducts, setCategorySampleProducts] = useState([]);
    const [showAddModal, setShowAddModal] = useState(false);
    const [showCategorySortedModal, setShowCategorySortedModal] = useState(false);
    const [categories, setCategories] = useState([]);
    const limitCategory = 5;
  
    const [widthOfPopUp, setWidthOfPopUp] = useState(90);
    const [heightOfPopUp, setHeightOfPopUp] = useState(90);
    const [animalTypeCategories, setAnimalTypeCategories] = useState({});
    const [alreadySelectedProductsForCategory, setAlreadySelectedProductsForCategory] = useState([]);
    const [editCategory, setEditCategory] = useState(null);
    const [editAnimalType, setEditAnimalType] = useState(null);
    const [editMode, setEditMode] = useState(false);
    
    // useEffect(() => {
    //    setCategorySampleProducts([
    //         {
    //           "id":1, "categoryName": "Dummy Category 1", "animalType": "Dog", "products":
    //             [
    //               {
    //                 "id": 1,
    //                 "name": "Dummy Product 1_1",
    //                 "description": "Dummy Description 1_1",
    //                 "image_url": "https://dummyimage.com/1203x503"
    //               },
    //               {
    //                 "id": 2,
    //                 "name": "Dummy Product 1_2",
    //                 "description": "Dummy Description 1_2",
    //                 "image_url": "https://dummyimage.com/1203x503"
    //               },
    //               {
    //                 "id": 3,
    //                 "name": "Dummy Product 1_3",
    //                 "description": "Dummy Description 1_3",
    //                 "image_url": "https://dummyimage.com/1203x503"
    //               }
    //               ,
    //               {
    //                 "id": 6,
    //                 "name": "Dummy Product 1_4",
    //                 "description": "Dummy Description 1_4",
    //                 "image_url": "https://dummyimage.com/1203x503"
    //               }
    //             ]
    //         },
    //         {
    //             "id":2, "categoryName": "Dummy Category 2", "animalType": "Cat", "products":
    //             [
    //               {
    //                 "id": 4,
    //                 "name": "Dummy Product 2_1",
    //                 "description": "Dummy Description 2_1",
    //                 "image_url": "https://dummyimage.com/1203x503"
    //               },
    //               {
    //                 "id": 5,
    //                 "name": "Dummy Product 2_2",
    //                 "description": "Dummy Description 2_2",
    //                 "image_url": "https://dummyimage.com/1203x503"
    //               }
    //             ]
    //         },
    //         {
    //             "id":3, "categoryName": "Dummy Category 3", "animalType": "Fish", "products":
    //             [
    //               {
    //                 "id": 7,
    //                 "name": "Dummy Product 3_1",
    //                 "description": "Dummy Description 3_1",
    //                 "image_url": "https://dummyimage.com/1203x503"
    //               },
    //               {
    //                 "id": 8,
    //                 "name": "Dummy Product 3_2",
    //                 "description": "Dummy Description 3_2",
    //                 "image_url": "https://dummyimage.com/1203x503"
    //               }
    //             ]
    //         }
    //       ]);
    
    // }, [])
    const fetchTopCategories = async () => {
      const response = await getTopCategories();
        if(response.isSuccess){
          if(Array.isArray(response.successResponse.data.data) && response.successResponse.data.data.length > 0){
            setCategorySampleProducts(response.successResponse.data.data);
          }else{
            setCategorySampleProducts([]);
          }
        }else{
          UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
        }
    };
    
    useEffect(() => {
      fetchTopCategories();    
    }, [])
    
    useEffect(() => {
        if(showAddModal){
            document.body.classList.toggle("overflow-hidden");
        }else{
            document.body.classList.remove("overflow-hidden"); 
        }
    }, [showAddModal])

    useEffect(() => {
      let categoriesArray = [];
      categorySampleProducts.map((data) => {
        let dummyObj = {...data};
        dummyObj.label = data.category.animal.name + " - " + data.category.name;
        categoriesArray.push(dummyObj);
      })
      setCategories(categoriesArray);
    }, [categorySampleProducts])

    useEffect(() => {
        if(categorySampleProducts.length >= limitCategory){
          ReactTooltip.rebuild();
        }else if(categorySampleProducts.length <= 1){
          ReactTooltip.rebuild();
        }
      }, [categorySampleProducts.length]);


    const openAddModal = () => {
      let categoryAnimalType = {};
      categorySampleProducts.map((data,index) => {
        let categories = [];
        if(categoryAnimalType.hasOwnProperty(data.category.animal.id)){
          categories = categoryAnimalType[data.category.animal.id];
        }
        if(!categories.includes(data.category.id)){
          categories.push(data.category.id);
        }
        categoryAnimalType[data.category.animal.id] = categories;
      })
      setAnimalTypeCategories(categoryAnimalType);
      setShowAddModal(true);
    }

    const closeAddModal = () => {
      setShowAddModal(false);
      setAlreadySelectedProductsForCategory([]);
      setEditAnimalType(null);
      setEditCategory(null);
      setEditMode(false);
    };

    const openCategorySortModel = () => {
      setShowCategorySortedModal(true);
    };

    const closeCategorySortModal = () => {
      setShowCategorySortedModal(false);
    };

    const onSaveOfCategorySort = (sortedArray) => {
      setCategorySampleProducts(sortedArray.map((data) => {
        delete data.label;
        return data;
      }))
      closeCategorySortModal();
    }

    const onSaveOfProducts = async (selectedProductsFromModal, selectedAnimalTypeFromModal, selectedCategoryFromModal) => {
      let productIds = [];
      selectedProductsFromModal.forEach((product) => {
        productIds.push(product.id);
      })

      console.log(productIds);
      let addTopCategoryResponse = await addTopCategory(selectedAnimalTypeFromModal.id, selectedCategoryFromModal.id, productIds);
      if(addTopCategoryResponse.isSuccess){
        await fetchTopCategories();
      }else{
        UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
      }
      closeAddModal();
    }

  const onEditOfProducts = async (selectedProductsFromModal, selectedAnimalTypeFromModal, selectedCategoryFromModal) => {
    let response = await deleteTopCategory(selectedCategoryFromModal.id);
    if (!response.isSuccess) {
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
      return;
    }

    let productIds = selectedProductsFromModal.map((product) => product.id);

    let addTopCategoryResponse = await addTopCategory(selectedAnimalTypeFromModal.id, selectedCategoryFromModal.id, productIds);
    if (addTopCategoryResponse.isSuccess) {
      await fetchTopCategories();
    } else {
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
    }
    closeAddModal();
  }

    const deleteSection = async (products, animal, category) => {
      let response = await deleteTopCategory(category.id);
      if (!response.isSuccess) {
        UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
        return;
      }else{
        await fetchTopCategories();
      }
    }

    const editSection = (products, animalType, categoryName) => {
      products.forEach((product) => {
        product.category = categoryName;
      })
      setAlreadySelectedProductsForCategory(products);
      setEditMode(true);
      setEditAnimalType(animalType);
      setEditCategory(categoryName);
      openAddModal();
    }

    const sortOrder = () => {
      openCategorySortModel();
    }

    return(
        <div className="p-4 flex-grow">
        <div className="flex items-center justify-center">

        {categorySampleProducts.length >= limitCategory ?
                <span data-tip={`Only ${limitCategory} of Category / Animal Types are allowed`} data-for="disabledAddButton" data-tip-disable={false}>
                <button
                  className="bg-blue-500 w-fit hover:bg-blue-700 items-start text-white font-bold py-2 px-4 rounded mt-4 disabled:opacity-25 disabled:cursor-not-allowed"
                  disabled={true}
                >
                  Add Animal Type / Category to below Component
                </button>
                </span>
                :
                <button
                className="bg-blue-500 w-fit hover:bg-blue-700 items-start text-white font-bold py-2 px-4 rounded mt-4"
                onClick={openAddModal}
              >
                Add Animal Type / Category to below Component
              </button>
        }

        <ReactTooltip id="disabledAddButton" place="bottom" effect="solid" />

        {categorySampleProducts.length <= 1 ?
                <span data-tip={`Sorting can be done when more than 1 category is selected in component`} data-for="disabledSortButton" data-tip-disable={false}>
                        <button
          className="bg-blue-500 w-fit hover:bg-blue-700 items-start text-white font-bold py-2 px-4 rounded mt-4 ml-4 disabled:opacity-25 disabled:cursor-not-allowed"
          disabled={true}
        >
          Sort Order
        </button>
                </span>
                :
                <button
                className="bg-blue-500 w-fit hover:bg-blue-700 items-start text-white font-bold py-2 px-4 rounded mt-4 ml-4"
                onClick={sortOrder}
              >
                Sort Order
              </button>
        }

<ReactTooltip id="disabledSortButton" place="bottom" effect="solid" />

        </div>
        <div className="flex flex-wrap items-center justify-center">
        {showAddModal && (

<Modal
  content={<HomeCategoryAddPopUp editMode={editMode} editAnimal={editAnimalType} editCategory={editCategory} alreadySelectedProductsForCategory={alreadySelectedProductsForCategory} existingAnimalTypeCategories={animalTypeCategories} onSaveOfProducts={editMode ? onEditOfProducts : onSaveOfProducts} />}
  onClose={closeAddModal}
  width={`${widthOfPopUp}%`}
  height={`${heightOfPopUp}%`}
  bgColorClass={"bg-white"}
  preventPredefinedDiv={true}
  isZoomEnabled={false}
/>

)}

{showCategorySortedModal && (

<Modal
  content={<Sorter data={categories} handleSaveFunction={onSaveOfCategorySort} />}
  onClose={closeCategorySortModal}
  width={`${widthOfPopUp}%`}
  height={`${heightOfPopUp}%`}
  bgColorClass={"bg-white"}
  preventPredefinedDiv={true}
  isZoomEnabled={false}
/>

)}
        </div>    
        <div>
            {categorySampleProducts.length > 0 && 
                    <HomeCategorySampleProducts key={JSON.stringify(categorySampleProducts)} categorySampleProducts={categorySampleProducts} handleDeleteSection={deleteSection} handleEditSection={editSection} showManipulateButtons={true} showEmptyMessage={true} />
            }
        </div>     
         </div>
    )
}

export default HomeCategory;