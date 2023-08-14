import React, { useEffect, useState } from "react";
import AddressForm from "../components/AddressForm";
import SavedAddressCard from "../components/SavedAddressCard";
import { Modal } from "react-responsive-modal";
import "react-responsive-modal/styles.css";
import { deleteAddress, getAddress, getAllAddress, saveAddress, selectDefaultAddress, updateAddress } from "../services/ApiClient";
import UINotification from "../components/UINotification";
import ReactTooltip from "react-tooltip";

const AddressPage = ({onCheckout, onSave, isCheckOutShown, isSaveShown, fromPage, isDefaultNeedsToBeGiven}) => {
  const [savedAddresses, setSavedAddresses] = useState([]);
  const [selectedAddressId, setSelectedAddressId] = useState(-1);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [isCheckoutDisabled, setIsCheckoutDisabled] = useState(true);
  const [isSaveDisabled, setIsSaveDisabled] = useState(true);
  const [isEditForm, setIsEditForm] = useState(false);
  const [editData, setEditData] = useState({});
  const [currentAddressId, setCurrentAddressId] = useState(-1);
  const maxNumberOfAddress = 5;

  const fetchSavedAddress = async () => {
    const response = await getAllAddress();
    if (response.isSuccess) {
      let data = response.successResponse.data.data;
      let isDefaultFound = false;
      let defaultAddressId = -1;
      for(let i = 0; i < data.length; i++){
        if(!isDefaultFound){
          isDefaultFound = data[i].isDefaultAddress;
          defaultAddressId = data[i].id;
        }else{
          data[i].isDefaultAddress = false;
        }
      }
      setSavedAddresses(data);
      setSelectedAddressId(defaultAddressId);
    } else {
      UINotification({message: "Issue Occured, Kindly try again later.", type: "Error"});
    }
  }

  useEffect(() => {
    fetchSavedAddress();
  }, []);

  useEffect(() => {
    ReactTooltip.rebuild();
  }, []);

  useEffect(() => {
    if(savedAddresses.length >= maxNumberOfAddress){
      ReactTooltip.rebuild();
    }
  }, [savedAddresses])

  const handleSaveAddress = async (newAddress) => {
    if(newAddress.isEditMode){
      const response = await updateAddress(newAddress.addressId, newAddress.address, newAddress.city, newAddress.state, newAddress.country, newAddress.pinCode);
      if (response.isSuccess) {
        await fetchSavedAddress();
        setModalIsOpen(false);
      } else {
        UINotification({message: "Issue Occured, Kindly try again later.", type: "Error"});
      }
    }else{
      const response = await saveAddress(newAddress.address, newAddress.city, newAddress.state, newAddress.country, newAddress.pinCode);
      if (response.isSuccess) {
        newAddress.id = response.successResponse.data.data.address_id;
        setSavedAddresses([newAddress, ...savedAddresses]);
        setModalIsOpen(false);
      } else {
        let failureResponse = response.failureResponse;
        if(response.statusCode === 400 && failureResponse.error_code === "MAXIMUM_RESOURCE_CREATED"){
          UINotification({message: "Maximum Number of Address Created for User", type: "Error"});
          setModalIsOpen(false);
          return;
        }
        UINotification({message: "Issue Occured, Kindly try again later.", type: "Error"});
      }
    }

  
  };

  const handleSelectedAddress = async(address_id) =>{
    if(isDefaultNeedsToBeGiven){
      const response = await selectDefaultAddress(address_id);
      if(response.isSuccess){
        setSelectedAddressId(address_id);
      }
    }else{
      setSelectedAddressId(address_id);
    }
    
  }

  const handleCheckout = () => {
    onCheckout();
  }

  const handleSave = () => {
    onSave();
  }

  const handleEdit = async (addressData) => {
    const response = await getAddress(addressData.address_id);
    if(response.isSuccess){
      let addressInfo = response.successResponse.data.data;
      let editData = {};
      editData.address = addressInfo.address;
      editData.pinCode = addressInfo.pincode;
      editData.country = addressInfo.country;
      editData.state = addressInfo.state;
      editData.city = addressInfo.city;
      setEditData(editData);
      setCurrentAddressId(addressData.address_id);
      setIsEditForm(true);
      setModalIsOpen(true);
    }
  }

  const handleDelete = async (addressData) => {
    const response = await deleteAddress(addressData.address_id);
    if (response.isSuccess) {
      await fetchSavedAddress();
    } else {
      UINotification({message: "Issue Occured, Kindly try again later.", type: "Error"});
    }
  }

  const handleOpenNewAddress = () => {
    setEditData({});
    setIsEditForm(false);
    setCurrentAddressId(-1);
    setModalIsOpen(true);
  }

  useEffect(() => {
    if(savedAddresses.length > 0 && selectedAddressId >= 0){
      setIsCheckoutDisabled(false);
      setIsSaveDisabled(false);
    }
  }, [savedAddresses, selectedAddressId]);

  return (
    <div className={`${fromPage === "Accounts" ? "mx-auto mt-5" : "mx-96"}`}>    
        <div className={`${fromPage === "Accounts" ? "border-2 mt-4" : "border-2 mt-4 mb-4"} p-4`}>
            <div className={`${fromPage === "Accounts" ? "" : "m-4"}`}>
          <h2 className=" text-xl font-bold mb-4 items-center justify-center flex">Saved Addresses</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-4">
            {savedAddresses.map((address, index) => (
              <SavedAddressCard
                key={index}
                address_id={address.id}
                address={address.address}
                city={address.city}
                country={address.country}
                state={address.state}
                pinCode={address.pincode}
                selected={address.id == selectedAddressId}
                isDefaultNeedsToBeGiven={isDefaultNeedsToBeGiven !== undefined ? isDefaultNeedsToBeGiven : false}
                onSelect={() => handleSelectedAddress(address.id)}
                isEditShown={true}
                isDeleteShown={true}
                handleEdit={handleEdit}
                handleDelete={handleDelete}
              />
            ))}
          </div>
          </div>
          </div>
        {savedAddresses.length < maxNumberOfAddress ? 
        (
          <span
          className="float-left cursor-pointer text-black font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          onClick={handleOpenNewAddress}
        >
          + Add Address
        </span>
        ) : 
        (
          <>
        <button
        className="opacity-25 cursor-not-allowed float-left text-black font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          data-for="disabledButton" data-tip-disable={false} data-tip={`Only ${maxNumberOfAddress} Address are allowed`}>
        + Add Address
      </button>
        <ReactTooltip id="disabledButton" place="bottom" effect="solid" />
          </>
        )
        }

          
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
      {modalIsOpen && (
        <Modal closeOnOverlayClick={false} open={modalIsOpen} onClose={() => setModalIsOpen(false)} center blockScroll={true} closeIconId={"addressModalClose"}  styles={{modal: {width: '50%'}}}>
      <div onClick={(event) => event.stopPropagation()}>
        <h1 className="text-xl font-bold mb-6 -mt-2">Enter your address</h1>
      <AddressForm onSave={handleSaveAddress} isEdit={isEditForm} editData={editData} addressId={currentAddressId} />
      </div>
      </Modal>
      )}
      
    </div>
  );
};

export default AddressPage;
