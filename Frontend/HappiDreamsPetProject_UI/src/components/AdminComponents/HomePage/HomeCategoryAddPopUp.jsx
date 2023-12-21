import Select, { components } from "react-select";
import Products from "../../../pages/Products";
import { useEffect, useState } from "react";
import Loading from "../../Loading";
import ReactTooltip from 'react-tooltip'
import { InformationCircleIcon } from "@heroicons/react/20/solid";
import { getAllAnimals, getAllCategories } from "../../../services/ApiClient";
import UINotification from "../../UINotification";

function HomeCategoryAddPopUp({existingAnimalTypeCategories, onSaveOfProducts, editMode, alreadySelectedProductsForCategory, editCategory, editAnimal}) {
    const [animalOptions, setAnimalOptions] = useState([]);
    const [categoryOptions, setCategoryOptions] = useState([]);
    const [selectedAnimalType, setSelectedAnimalType] = useState({id: -1, name: "Select a Animal Type"});
    const [selectedCategory, setSelectedCategory] = useState({id: -1, name: "Select a Category"});
    const [isCategoryLoading, setIsCategoryLoading] = useState(false);
    const [isProductLoading, setIsProductLoading] = useState(false);
    const [maxHeight, setMaxHeight] = useState(0);
    const [maxCheckedProducts, setMaxCheckedProducts] = useState(6);
    const [selectedProducts, setSelectedProducts] = useState([]);
    const [checkedIds, setCheckedIds] = useState([]);
    const [productRefreshKey, setProductRefreshKey] = useState(1);

    useEffect(() => {
      setSelectedProducts(alreadySelectedProductsForCategory);
      let alreadySelectedProducts = [];
      alreadySelectedProductsForCategory.map((data, index) => {
        alreadySelectedProducts.push(data.id);
      });
      setCheckedIds(alreadySelectedProducts);
    }, []);

    useEffect(() => {
      if(editMode !== undefined && editMode){
        let alreadySelectedProducts = [];
        selectedProducts.map((data, index) => {
          alreadySelectedProducts.push(data.id);
        });
        setCheckedIds(alreadySelectedProducts);
        setProductRefreshKey(productRefreshKey + 1);
      }
    }, [selectedProducts]);

    useEffect(() => {
        // Calculate the available height for the Products div
        const calculateMaxHeight = () => {
           if(selectedCategory !== null && selectedCategory.id >= 0 && !isProductLoading){
              const titleHeight = document.getElementById("titleText").offsetHeight + parseInt(window.getComputedStyle(document.getElementById("titleText")).marginTop, 10) + parseInt(window.getComputedStyle(document.getElementById("titleText")).marginBottom, 10);
              const animalAndCategoryTypeHeight = document.getElementById("animalAndCategoryType").offsetHeight + parseInt(window.getComputedStyle(document.getElementById("animalAndCategoryType")).marginTop, 10) + parseInt(window.getComputedStyle(document.getElementById("animalAndCategoryType")).marginBottom, 10);
              const productsTextHeight = document.getElementById("productsText").offsetHeight + parseInt(window.getComputedStyle(document.getElementById("productsText")).marginTop, 10) + parseInt(window.getComputedStyle(document.getElementById("productsText")).marginBottom, 10);
              const overallDiv = document.getElementById("predefinedModal").offsetHeight;

              const availableHeight = overallDiv - (titleHeight + animalAndCategoryTypeHeight + productsTextHeight + 40);
              setMaxHeight(availableHeight);
           }
        };
    
        // Calculate the initial height and update on window resize
        calculateMaxHeight();
        window.addEventListener("resize", calculateMaxHeight);
    
        // Clean up the event listener on component unmount
        return () => {
          window.removeEventListener("resize", calculateMaxHeight);
        };
      }, [selectedCategory, isProductLoading]);

      const setCheckBoxDataFromProducts = () => {
        // let checkBoxArray = [];
        // products.map((product) => {
        //   if(product.animalType === selectedAnimalType.label && product.categoryName === selectedCategory.label){
        //     checkBoxArray.push(product.id);
        //   }
        // })
        // setCheckBoxIdsArray(checkBoxArray);
      } 
  
      const addProduct = (product) => {
        setSelectedProducts(prevProducts => {
          let isProductExists = prevProducts.some((productData) => productData.id === product.id);
          if(!isProductExists){
            const updatedProducts = [...prevProducts];
            const data = { category: selectedCategory };
            const newProduct = { ...product, ...data };
            updatedProducts.push(newProduct);
            return updatedProducts;
          }else{
            return prevProducts;
          }
        });
      }
    
      const removeProduct = (productData) => {
        setSelectedProducts(prevProducts => prevProducts.filter((product) => {
          return !(product.id === productData.id && product.category.animal.id === selectedAnimalType.id && product.category.id === selectedCategory.id);
        }));
      }
      
      const handleCheckBox = (checkboxToggle, product) => {
        if(checkboxToggle){
          addProduct(product);
        }else{
          removeProduct(product);
        }
      }

      const manipulateCategoryData = (categoryData) => {
        if(existingAnimalTypeCategories.hasOwnProperty(selectedAnimalType.id)){
          categoryData.map((data, index) => {
            if(existingAnimalTypeCategories[selectedAnimalType.id].includes(data.id)){
              data.disabled = true;
            }else{
              data.disabled = false;
            }
          })
        }
        return categoryData;
      }

      const fetchAllAnimals = async() => {
        const allAnimalsData = await getAllAnimals();
        if(allAnimalsData.isSuccess){
          setAnimalOptions(allAnimalsData.successResponse.data.data);
        }else{
          setAnimalOptions([]);
          UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
        }
      }

      const fetchAllCategories = async() => {
        const allCategoriesData = await getAllCategories(selectedAnimalType.id);
        if(allCategoriesData.isSuccess){
          setCategoryOptions( manipulateCategoryData(allCategoriesData.successResponse.data.data) );
        }else{
          UINotification({ message: "Issue Occured, while collecting Categories Data", type: "Error" });
        }
      }

      const getOptionLabel = (option) => {
        if(option.hasOwnProperty("name")){
          return option.name;
        }else if(option.hasOwnProperty("label")){
          return option.label;
        }
        return null;
      }
  
      useEffect(() => {
        fetchAllAnimals();
      }, []);

      useEffect(() => {
        if(editMode !== undefined && editMode){
          if(animalOptions !== undefined && animalOptions.length > 0){
            setSelectedAnimalType(animalOptions.find(data => data.id === editAnimal.id));
          }
        }
      }, [animalOptions])
  
      useEffect(() => {
        if (selectedAnimalType && selectedAnimalType.id >= 0) {
          fetchAllCategories();
          setSelectedCategory({id: -1, name: "Select a Category"});
          setIsCategoryLoading(false);
        }
      }, [selectedAnimalType]);

      useEffect(() => {
        if(editMode !== undefined && editMode){
          if(categoryOptions !== undefined && categoryOptions.length > 0){
            setSelectedCategory(categoryOptions.find(data => data.id === editCategory.id));
          }
        }
      }, [categoryOptions])

      useEffect(() => {
        if(selectedAnimalType && selectedCategory && selectedAnimalType.id >= 0 && selectedCategory.id >= 0){
         setIsProductLoading(true);
         if(!(editMode !== undefined && editMode)){
          setSelectedProducts([]);
         }
        }
        setIsProductLoading(false);
       }, [selectedAnimalType, selectedCategory]);

    const customStyles = {
        control: (provided) => ({
          ...provided,
          background: 'white',
          cursor: 'pointer'
        }),
        option: (provided, state) => ({
            ...provided,
            cursor: state.isDisabled ? 'not-allowed' : 'pointer',
            width: state.isDisabled ? '100%' : 'auto'
          })
      };

      const DropdownMenuList = (props) => {
        if (!props.children || props.children.length === undefined || props.children.length === 0) {
          return <components.MenuList {...props} />;
        }
        return (
          <components.MenuList {...props}>
            {props.children.map((child) => {
              if (child.props.isDisabled) {
                return (
                  <div
                    key={child.key}
                    style={{
                      position: 'relative',
                      display: 'inline-block',
                      width: '100%'
                    }}
                  >
                    <span data-tip="This Category is already present in below Component" data-for={child.key}>
                      {child}
                    </span>
                    <ReactTooltip id={child.key} effect="solid" />
                  </div>
                );
              }
              return child;
            })}
          </components.MenuList>
        );
      };
      
      const MenuList = (props) => <DropdownMenuList {...props} />;

    return (
        <>
            <div className="h-full" id="overallDiv">
                <h3 className="font-bold text-lg flex items-center justify-center mt-10" id="titleText">Add Animal Type and Category to Component</h3>
                <div className="w-full m-5 mb-10 flex flex-row items-center bg-white" id="animalAndCategoryType">
                    <div className="flex items-start ml-20 flex-col items-center justify-center">
                        <div className="flex items-center justify-center mb-2">Animal Type</div>
                        <div className="flex items-center justify-center">
                            <Select options={animalOptions} isDisabled={editMode !== undefined && editMode}
                                placeholder={"Select a Animal Type"} onChange={(selectedValue) => { setSelectedAnimalType(selectedValue); if(selectedValue !== selectedAnimalType){setIsCategoryLoading(true);}}} styles={customStyles} getOptionLabel={getOptionLabel} getOptionValue={(option) => option.id} value={animalOptions.find((c) => c.id === selectedAnimalType.id)} />
                        </div>

                    </div>
                    <div className="flex items-end ml-auto mr-20 flex-col items-center justify-center">
                        <div className="flex items-center justify-center mb-2">Category</div>
                        <div className="flex items-center justify-center">
                            <Select options={categoryOptions}  isDisabled={editMode !== undefined && editMode}
                            isLoading={isCategoryLoading} isOptionDisabled={(option) => option.disabled}  components={{ MenuList  }} getOptionLabel={getOptionLabel} placeholder={isCategoryLoading ? "Loading..." : "Select a Category"} onChange={(selectedValue) => { if(selectedValue !== selectedCategory){setIsProductLoading(true);} setSelectedCategory(selectedValue) }} styles={customStyles} getOptionValue={(option) => option.id} value={categoryOptions.find((c) => c.id === selectedCategory.id)} />
                        </div>
                    </div>
                </div>
                {selectedCategory !== null && selectedCategory.id >= 0 &&
                    <div className={``}>
                        {isProductLoading ? <Loading /> : <>
                           <h3 className="font-bold text-lg flex items-center justify-center mb-2 mt-5" id="productsText">
                            Products
                            <InformationCircleIcon data-tip={`Max ${maxCheckedProducts} products can be selected`} data-for="informationIcon" className="mt-1 h-4 w-4 text-gray-500 flex items-center justify-center" />
                            <ReactTooltip id="informationIcon" effect="solid" />
                           </h3>

                            {/* max-h-[600px] */}
                            <div className={`overflow-y-auto`} style={{ maxHeight: `${maxHeight}px` }} >
                                <Products key={productRefreshKey} isAdminPanelUsage={true} checkedBoxIds={checkedIds} handleCheckBox={(product, checkboxToggle) => { handleCheckBox(checkboxToggle, product) }} preventProductNavigation={true} hideTitleVisibility={true} hideSortVisibility={true} category_info_from_components={selectedCategory} animal_info_from_components={selectedAnimalType} maxCheckedForCheckBox={maxCheckedProducts} />
                            </div>
                        </>}
                    </div>
                }
{selectedCategory !== null && selectedCategory.id >= 0 &&
<div className=" flex items-center justify-center">
        <button
          className="bg-green-500 w-36 hover:bg-green-700 text-white font-bold py-2 px-4 rounded mt-4 disabled:opacity-25 disabled:cursor-not-allowed"
          onClick={() => {onSaveOfProducts(selectedProducts, selectedAnimalType, selectedCategory)}}
          disabled={selectedProducts.length <= 0}
        >
          Save
        </button>
        </div>
         }

            </div>
        </>
    )
}

export default HomeCategoryAddPopUp;