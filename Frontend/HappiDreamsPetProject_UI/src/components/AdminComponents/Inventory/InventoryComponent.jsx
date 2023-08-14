import React, { useEffect, useState } from 'react';
import { Transition } from 'react-transition-group';
import { ChevronLeftIcon, ChevronRightIcon } from '@heroicons/react/20/solid';
import { ArrowLeftIcon } from "@heroicons/react/20/solid";
import Select from "react-select";
import TabBar from "../../TabBar";

const InventoryComponent = ({inventoryType, handleSave, selectedOption, tabs, allData, onGoBack}) => {
  const [renderedTabContent, setRenderedTabContent] = useState();
  const [isSideBarVisible, setSideBarVisible] = useState(true);
  const [isSideBarInitiated, setIsSideBarInitiated] = useState(true);
  const [isSideBarContentVisible, setIsSideBarContentVisible] = useState(true);
  const [type, setType] = useState("");
  const [removeFlexClass, setRemoveFlexClass] = useState(false);
  const [animalOptions, setAnimalOptions] = useState(allData);
  const [categoryOptions, setCategoryOptions] = useState(allData);

  const [selectedAnimalType, setSelectedAnimalType] = useState({id: -1, name: "Select a Animal Type", label: 'Select a Animal Type'});

  const [selectedCategory, setSelectedCategoryType] = useState({id: -1, name: "Select a Category", label: 'Select a Category'});

  useEffect(() => {
    setType(setTypeFromInventoryType());
  }, []);
  useEffect(() => {
    setAnimalOptions(allData);
    setCategoryOptions(allData);
  }, [allData])

  useEffect(() => {
    if(!(selectedOption === undefined || selectedOption === null)){
      if(type === "animal"){
        setSelectedAnimalType(selectedOption);
      }else if(type === "category"){
        setSelectedCategoryType(selectedOption);
      }
    }
  }, [type, selectedOption])

  const getPlaceholder = () => {
    if(type === "animal"){
      return selectedAnimalType.name;
    }else if(type === "category"){
      return selectedCategory.name;
    }
    return null;
  }

  const handleChangeInDropDown = (selectedValue) => {
    if(type === "animal"){
      setSelectedAnimalType(selectedValue);
    }else if(type === "category"){
      setSelectedCategoryType(selectedValue);
    }
  }

  const getValueForDropDown = () => {
    if(type === "animal"){
      return selectedAnimalType;
    }else if(type === "category"){
      return selectedCategory;
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

  const customStyles = {
    control: (provided, state) => ({
      ...provided,
      background: 'white', // Change background color when focused
      cursor: 'pointer'
    }),
    option: (provided, state) => ({
        ...provided,
        cursor: 'pointer'
      })
  };

  const setTypeFromInventoryType = () => {
    if(inventoryType !== undefined){
      switch(inventoryType.toLowerCase()){
        case "animal":
        case "animals":
          return "animal";
        case "category":
        case "categories":
          return "category";
        case "product":
        case "products":
          return "product";
        default:
          return "product";
      }
    }else{
      return "product";
    }
  }

  const handleSaveProgress = () => {
    // Save progress logic here
    if(type === "animal"){
      handleSave(selectedAnimalType);
    }else if(type === "category"){
      handleSave(selectedCategory);
    }
   
  };

  const fetchTitleFromInventoryType = () => {
    switch(type.toLowerCase()){
      case "animal":
        return "Animal Type";
      case "category":
        return "Category";
      case "product":
        return "Product";
      default:
        return "Product";
    }
  }

  const handleToggleBottomBar = () => {
    setSideBarVisible(!isSideBarVisible);
    setIsSideBarInitiated(!isSideBarInitiated);
  };

  const handleOnEntered = () => {
    setIsSideBarContentVisible(!isSideBarContentVisible);
  };

  const handleTabClick = (content, tabData) => {
      if(tabData.hasOwnProperty("removeFlex") && tabData.removeFlex){
        setRemoveFlexClass(true);
      }else{
        setRemoveFlexClass(false);
      }
      setRenderedTabContent(content);
  }

  return (
    <div className='flex flex-row h-full'>
    <div className={`flex-grow ${isSideBarVisible ? "pl-5 pt-5 pb-5" : "p-5"}`}>
    <div className="flex flex-col">
      <div className="flex flex-row items-center justify-center">
    <span className='mr-2'><ArrowLeftIcon className="w-7 h-7 cursor-pointer" onClick={onGoBack ? typeof onGoBack === "function" ? onGoBack : null : null} /></span>
      <span className='flex-grow'><TabBar tabs={tabs} onTabClick={(content, tabData) => {handleTabClick(content, tabData)}} /></span>
      </div>
      <div className={`${removeFlexClass ? "" : "flex items-center justify-center"}`}>
        {renderedTabContent}
      </div>
    </div>
   
    </div>
    <div className='flex items-center justify-center'>
      <div className='flex items-center justify-center'>
      <Transition
        in={!isSideBarInitiated}
        timeout={300}
        mountOnEnter
        unmountOnExit
        classNames="fade"
      >
        {(state) => (
          <div
            className={`${
              state === 'entered' ? '' : 'hidden'
            } transition-opacity duration-300 cursor-pointer`}
            onClick={handleToggleBottomBar}
          >
            <ChevronLeftIcon className={`w-5 h-5 mt-[4.3px] transition-transform`} />
          </div>
        )}
      </Transition>
      </div>      
      <div className='h-full flex-grow'>
      <Transition
        in={isSideBarVisible}
        timeout={300}
        mountOnEnter
        unmountOnExit
        classNames="fade"
        onEntered={handleOnEntered}
        onExited={handleOnEntered}
      >
        {(state) => (
          <div
            className={`bg-gray-200 shadow-lg p-4 rounded-t-lg transition-transform duration-300 transform h-full ${
              state === 'entered' ? 'translate-x-0' : 'translate-x-full'
            }`}
          >
            <div className='flex flex-row flex-grow h-full'>
             <div className="flex items-center justify-center flex-grow mr-4">
             <button onClick={handleToggleBottomBar}>
                <ChevronRightIcon className={`w-6 h-6 mt-1 transition-transform`}  />
              </button>
             </div>
             <div className='flex items-center justify-center flex-col'>
            <div>
              <h3 className="text-lg font-semibold">Select a {fetchTitleFromInventoryType()}</h3>
            </div>
            <div className="mt-1">
            <Select options={type === "animal" ? animalOptions : type === "category" ? categoryOptions : []}
      placeholder={getPlaceholder()} getOptionLabel={getOptionLabel} onChange={(selectedValue) => {handleChangeInDropDown(selectedValue)}} styles={customStyles}  getOptionValue={(option) => option.id} value={getValueForDropDown()}  />
             <div className='flex items-center justify-center'>
             <button
          className={`bg-green-500 w-36 hover:bg-green-700 text-white font-bold py-2 px-4 rounded mt-4 ${handleSave !== undefined ? "" : "hidden"} disabled:opacity-25 disabled:cursor-not-allowed`} 
          onClick={handleSaveProgress}
          disabled={type === "animal" ? selectedAnimalType.id <= 0 : selectedCategory.id <= 0}
        >
          Save
        </button>
        </div>
            </div>
            </div>
            </div>
          </div>
        )}
      </Transition>
      </div>
    </div>
    </div>
  );
};

export default InventoryComponent;
