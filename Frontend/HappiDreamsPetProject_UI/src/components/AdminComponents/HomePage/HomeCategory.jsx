import { useEffect, useState } from "react";
import HomeCategorySampleProducts from "../../../pages/HomeCategorySampleProducts";
import ReactTooltip from 'react-tooltip'
import Modal from "../../Modal";
import HomeCategoryAddPopUp from "./HomeCategoryAddPopUp";
import Sorter from "../Sorter";

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
    
    useEffect(() => {
       setCategorySampleProducts([
            {
              "id":1, "categoryName": "Dummy Category 1", "animalType": "Dog", "products":
                [
                  {
                    "id": 1,
                    "name": "Dummy Product 1_1",
                    "description": "Dummy Description 1_1",
                    "image_url": "https://dummyimage.com/1203x503"
                  },
                  {
                    "id": 2,
                    "name": "Dummy Product 1_2",
                    "description": "Dummy Description 1_2",
                    "image_url": "https://dummyimage.com/1203x503"
                  },
                  {
                    "id": 3,
                    "name": "Dummy Product 1_3",
                    "description": "Dummy Description 1_3",
                    "image_url": "https://dummyimage.com/1203x503"
                  }
                  ,
                  {
                    "id": 6,
                    "name": "Dummy Product 1_4",
                    "description": "Dummy Description 1_4",
                    "image_url": "https://dummyimage.com/1203x503"
                  }
                ]
            },
            {
                "id":2, "categoryName": "Dummy Category 2", "animalType": "Cat", "products":
                [
                  {
                    "id": 4,
                    "name": "Dummy Product 2_1",
                    "description": "Dummy Description 2_1",
                    "image_url": "https://dummyimage.com/1203x503"
                  },
                  {
                    "id": 5,
                    "name": "Dummy Product 2_2",
                    "description": "Dummy Description 2_2",
                    "image_url": "https://dummyimage.com/1203x503"
                  }
                ]
            },
            {
                "id":3, "categoryName": "Dummy Category 3", "animalType": "Fish", "products":
                [
                  {
                    "id": 7,
                    "name": "Dummy Product 3_1",
                    "description": "Dummy Description 3_1",
                    "image_url": "https://dummyimage.com/1203x503"
                  },
                  {
                    "id": 8,
                    "name": "Dummy Product 3_2",
                    "description": "Dummy Description 3_2",
                    "image_url": "https://dummyimage.com/1203x503"
                  }
                ]
            }
          ]);
    
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
        dummyObj.label = data.animalType + " - " + data.categoryName;
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
        if(categoryAnimalType.hasOwnProperty(data.animalType)){
          categories = categoryAnimalType[data.animalType];
        }
        if(!categories.includes(data.categoryName)){
          categories.push(data.categoryName);
        }
        categoryAnimalType[data.animalType] = categories;
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

    const onSaveOfProducts = (selectedProductsFromModal, selectedAnimalTypeFromModal, selectedCategoryFromModal) => {
      //dummy id
      let id = categorySampleProducts.length + 1;
      //dummymanipulate
      selectedProductsFromModal = selectedProductsFromModal.map((data) => {
        return{
        ...data,
        image_url: data.image
      };
      })
      console.log(selectedProductsFromModal);
      let array = [...categorySampleProducts];
      array.push({id: id, categoryName: selectedCategoryFromModal.label, animalType: selectedAnimalTypeFromModal.label, products: selectedProductsFromModal});
      setCategorySampleProducts(array);
      closeAddModal();
    }

    const onEditOfProducts = (selectedProductsFromModal, selectedAnimalTypeFromModal, selectedCategoryFromModal) => {
      //dummymanipulate
      selectedProductsFromModal = selectedProductsFromModal.map((data) => {
        return{
        ...data,
        image_url: data.hasOwnProperty("image_url") ? data.image_url : data.image
      };
      })
      console.log(selectedProductsFromModal);
      let array = [...categorySampleProducts];
      array =  array.map((data) => {
        if(data.animalType === selectedAnimalTypeFromModal.label && data.categoryName === selectedCategoryFromModal.label){
          data.products = selectedProductsFromModal;
        }
        return data;
      })
      setCategorySampleProducts(array);
      closeAddModal();
    }

    const deleteSection = (id, animalType, categoryName) => {
      let dataArray = [...categorySampleProducts];
      dataArray = dataArray.filter((data) => {
        return !(data.id === id && data.animalType === animalType && data.categoryName === categoryName);
      });
      setCategorySampleProducts(dataArray);
    }

    const editSection = (products, animalType, categoryName) => {
      setAlreadySelectedProductsForCategory(products);
      setEditMode(true);
      setEditAnimalType(animalType);
      setEditCategory(categoryName);
      openAddModal();
    }

    const sortOrder = () => {
      console.log("Dudd");
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
  content={<HomeCategoryAddPopUp editMode={editMode} animalType={editAnimalType} category={editCategory} alreadySelectedProductsForCategory={alreadySelectedProductsForCategory} existingAnimalTypeCategories={animalTypeCategories} onSaveOfProducts={editMode ? onEditOfProducts : onSaveOfProducts} />}
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