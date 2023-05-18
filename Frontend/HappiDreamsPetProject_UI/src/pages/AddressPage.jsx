import React, { useEffect, useState } from "react";
import AddressForm from "../components/AddressForm";
import SavedAddressCard from "../components/SavedAddressCard";
import { Modal } from "react-responsive-modal";
import "react-responsive-modal/styles.css";

const AddressPage = ({onCheckout, onSave, isCheckOutShown, isSaveShown, fromPage, defaultSelectAddressIndex, isDefaultNeedsToBeGiven}) => {
  const [savedAddresses, setSavedAddresses] = useState([]);
  const [selectedAddressIndex, setSelectedAddressIndex] = useState(defaultSelectAddressIndex !== undefined ? defaultSelectAddressIndex : -1);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [isCheckoutDisabled, setIsCheckoutDisabled] = useState(true);
  const [isSaveDisabled, setIsSaveDisabled] = useState(true);

  const handleSaveAddress = (newAddress) => {
    setSavedAddresses([...savedAddresses, newAddress]);
    setModalIsOpen(false);
  };

  const handleSelectedAddress = (index) =>{
    setSelectedAddressIndex(index);
  }

  const handleCheckout = () => {
    onCheckout();
  }

  const handleSave = () => {
    onSave();
  }

  useEffect(() => {
    if(savedAddresses.length > 0 && selectedAddressIndex >= 0){
      setIsCheckoutDisabled(false);
      setIsSaveDisabled(false);
    }
  }, [savedAddresses, selectedAddressIndex]);

  return (
    <div className={`${fromPage === "Accounts" ? "mx-auto mt-5" : "mx-96"}`}>    
        <div className={`${fromPage === "Accounts" ? "border-2 mt-4" : "border-2 mt-4 mb-4"} p-4`}>
            <div className={`${fromPage === "Accounts" ? "" : "m-4"}`}>
          <h2 className=" text-xl font-bold mb-4 items-center justify-center flex">Saved Addresses</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {savedAddresses.map((address, index) => (
              <SavedAddressCard
                key={index}
                address={address.address}
                city={address.city}
                country={address.country}
                state={address.state}
                zipCode={address.zipCode}
                selected={index == selectedAddressIndex}
                isDefaultNeedsToBeGiven={isDefaultNeedsToBeGiven !== undefined ? isDefaultNeedsToBeGiven : false}
                onSelect={() => handleSelectedAddress(index)}
              />
            ))}
          </div>
          </div>
          </div>
        
          <span
        className="float-left cursor-pointer text-black font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        onClick={() => setModalIsOpen(true)}
      >
        + Add Address
      </span>
     {isCheckOutShown && <button
        className={`text-white font-bold py-2 px-4 rounded float-right ${isCheckoutDisabled ? "bg-gray-400 cursor-not-allowed" : "bg-purple-500  hover:bg-purple-900 cursor-pointer focus:outline-none focus:shadow-outline "}`}
        onClick={handleCheckout}
        disabled={isCheckoutDisabled}
      >
        Checkout
      </button>} 
      {isSaveShown && 
      <button
      className={`text-white font-bold py-2 px-4 rounded mt-1 float-right ${isSaveDisabled ? "bg-gray-400 cursor-not-allowed" : "bg-purple-500  hover:bg-purple-900 cursor-pointer focus:outline-none focus:shadow-outline "}`}
      onClick={handleSave}
      disabled={isSaveDisabled}
    >
      Save
    </button>
      }
      <Modal open={modalIsOpen} onClose={() => setModalIsOpen(false)} center blockScroll={true} closeIconId={"addressModalClose"}  styles={{modal: {width: '50%'}}}>
        <h1 className="text-xl font-bold mb-6 -mt-2">Enter your address</h1>
      <AddressForm onSave={handleSaveAddress} />
      </Modal>
    </div>
  );
};

export default AddressPage;
