import React, { useEffect, useState } from 'react';
import { Transition } from 'react-transition-group';
import { ChevronLeftIcon, ChevronRightIcon } from '@heroicons/react/20/solid';
import Select from "react-select";
import TabBar from '../../Tabbar';

const InventoryComponent = ({inventoryType, handleSave, selectedOption, tabs}) => {
  const [renderedTabContent, setRenderedTabContent] = useState();
  const [isSideBarVisible, setSideBarVisible] = useState(true);
  const [isSideBarInitiated, setIsSideBarInitiated] = useState(true);
  const [isSideBarContentVisible, setIsSideBarContentVisible] = useState(true);
  const [type, setType] = useState("");

  const animalOptions = [
    { id: 1, name: 'Cat', label: 'Cat' },
    { id: 2, name: 'Dog', label: 'Dog' },
    { id: 3, name: 'Fish', label: 'Fish' }];
  const [selectedAnimalType, setSelectedAnimalType] = useState({id: -1, name: "Select a Animal Type", label: 'Select a Animal Type'});

  const categoryOptions = [
    { id: 1, label: 'Dummy Category 1', name: 'Dummy Category 1' },
    { id: 2, label: 'Dummy Category 2', name: 'Dummy Category 2' },
    { id: 3, label: 'Dummy Category 3', name: 'Dummy Category 3' }];
  const [selectedCategory, setSelectedCategoryType] = useState({id: -1, name: "Select a Category", label: 'Select a Category'});

  useEffect(() => {
    setType(setTypeFromInventoryType());
  }, [])

  useEffect(() => {
    if(!(selectedOption === undefined || selectedOption === null)){
      if(type === "animal"){
        setSelectedAnimalType(selectedOption);
      }else if(type === "category"){
        setSelectedCategoryType(selectedOption);
      }
    }
  }, [type, selectedOption])


  const getOptions = () => {
    if(type === "animal"){
      return animalOptions;
    }else if(type === "category"){
      return categoryOptions;
    }
    return null;
  }

  const getPlaceholder = () => {
    if(type === "animal"){
      return selectedAnimalType.label;
    }else if(type === "category"){
      return selectedCategory.label;
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

  const handleTabClick = (tab) => {
    setActiveTab(tab);
  };

  const handleToggleBottomBar = () => {
    setSideBarVisible(!isSideBarVisible);
    setIsSideBarInitiated(!isSideBarInitiated);
  };

  const handleOnEntered = () => {
    setIsSideBarContentVisible(!isSideBarContentVisible);
  };

  return (
    <div className='flex flex-row h-full'>
    <div className={`flex-grow ${isSideBarVisible ? "pl-10 pt-10 pb-10" : "p-10"}`}>
    <div className="flex flex-col">
      <TabBar tabs={tabs} onTabClick={(content) => {setRenderedTabContent(content)}} />
      <div className='flex items-center justify-center'>
        {renderedTabContent}
      </div>
    </div>
   
    </div>
    <div className='flex items-center justify-center'>
      <div className='flex items-center justify-center'>
      {/* <button onClick={handleToggleBottomBar} className={`${isBottomBarVisible ? "hidden" : ""}`}><ChevronLeftIcon className={`w-5 h-5 mt-[4.3px] transition-transform`} /></button> */}
      
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
            <Select options={getOptions()}
      placeholder={getPlaceholder()} onChange={(selectedValue) => {handleChangeInDropDown(selectedValue)}} styles={customStyles}  getOptionValue={(option) => option.id} value={getValueForDropDown()}  />
             <div className='flex items-center justify-center'>
             <button
          className={`bg-green-500 w-36 hover:bg-green-700 text-white font-bold py-2 px-4 rounded mt-4 ${handleSave !== undefined || selectedAnimalType.id > 0 ? "" : "hidden"}`}
          onClick={handleSaveProgress}
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
