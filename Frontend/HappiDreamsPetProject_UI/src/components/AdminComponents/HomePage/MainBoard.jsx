import { useEffect, useRef, useState } from "react";
import ImageThumbnail from "../ImageThumbnail";
import Modal from "../../Modal";
import MainPageCarousel from "../../MainPageCarousel";
import React from "react";
import ReactTooltip from 'react-tooltip'
import { addPromotions, deletePromotion, getPromotions } from "../../../services/ApiClient";
import UINotification from "../../UINotification";

function MainBoard(){
    const [images, setImages] = useState([]);
    const [showPreviewModal, setShowPreviewModal] = useState(false);
    // const [disableSaveButton, setDisableSaveButton] = useState(true);
    const [disablePreviewButton, setDisablePreviewButton] = useState(true);
    const maxNumberOfImages = 5;

    const fetchImages = async () => {
      const response = await getPromotions();
      if(response.isSuccess){
        if(Array.isArray(response.successResponse.data.data)){
          if(response.successResponse.data.data.length > 0){
            let imageApiData = response.successResponse.data.data;
            imageApiData.forEach((data) => {
              data.url = data.imageUrl.replaceAll("static/", "");
              data.url = "assets/" + data.url;
              data.imageUrl = data.url;
            })
            setImages(imageApiData);
            setDisablePreviewButton(false);
          }else{
            setImages([]);
            setDisablePreviewButton(true);
          }
        }
      }else{
        UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
      }
  };

    useEffect(() => {
      fetchImages();
    }, [])

    useEffect(() => {
      if(images.length >= 5){
        ReactTooltip.rebuild();
      }
    }, [images.length])
  
    const handleDeleteImage = async (imageId) => {
      //setImages(images.filter((image) => image.id !== imageId));
      const deleteImageResponse = await deletePromotion(imageId);
      if(deleteImageResponse.isSuccess){
        UINotification({ message: "Promotion Deleted Successfully", type: "Success" });
        fetchImages();    
      }else{
        UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
      }
    };

    useEffect(() => {
      ReactTooltip.rebuild();
    }, []);
 
    const handleImageUpload = async(newImage) => {
      const imageUpload = await addPromotions(newImage);
      if(imageUpload.isSuccess){
        UINotification({ message: "Promotion Added Successfully", type: "Success" });
        fetchImages();
      }else{
        UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
      }
    };
  
    const handleTogglePreviewModal = () => {
      setShowPreviewModal(!showPreviewModal);
    };
  
    return (
      <div className="p-4 flex-grow">
        <h1 className="text-2xl font-bold mb-4 flex justify-center items-center">Main Board Customization</h1>
  
        {/* Image Thumbnails */}
        <div className="flex flex-wrap -mx-2 justify-center items-center ">
          {images.map((image) => (
            <ImageThumbnail
              key={image.id}
              image={image}
              onDelete={handleDeleteImage}
              showName={true}
            />
          ))}
        </div>

        <div className="flex flex-wrap items-center justify-center gap-x-40">
              
        {/* Upload Image Button */}
        <button
          className="bg-blue-500 w-36 hover:bg-blue-700 items-start text-white font-bold py-2 px-4 rounded mt-4 disabled:opacity-25 disabled:cursor-not-allowed"
          onClick={handleTogglePreviewModal}
          disabled={disablePreviewButton}
        >
          Show Preview
        </button>

        {images.length < maxNumberOfImages && <ImageUploader onImageUpload={handleImageUpload} />}
        {images.length >= maxNumberOfImages && <>
          <span data-tip={`Only ${maxNumberOfImages} images are allowed`} data-for="disabledButton" data-tip-disable={false}>
        <button
        className="disabled:opacity-25 disabled:cursor-not-allowed bg-gray-800 text-white font-bold items-center w-36 py-2 px-4 rounded mt-4"
          disabled={true}>
        Upload Image
      </button>
      </span>
        <ReactTooltip id="disabledButton" place="bottom" effect="solid" />
        </>}
       

        {/* Upload Image Modal */}
        {showPreviewModal && (

          <Modal
            content={<MainPageCarousel prePopulateImages={images} isPopUp={true} />}
            onClose={handleTogglePreviewModal}
            isZoomEnabled={false}
          />

        )}
  
        {/* Save Progress Button */}
        {/* <button
          className="bg-green-500 w-36 hover:bg-green-700 text-white font-bold py-2 px-4 rounded mt-4 disabled:opacity-25 disabled:cursor-not-allowed"
          onClick={handleSaveProgress}
          disabled={disableSaveButton}
        >
          Save
        </button> */}
        </div>
      </div>
    );

}

function ImageUploader({ onImageUpload }) {
  const fileInputRef = useRef(null);

  const handleFileSelect = async (event) => {
    const file = event.target.files[0];
    await onImageUpload(file);
    fileInputRef.current.value = '';
  };


  return (
    <div className="flex justify-center items-center">
      <input
        type="file"
        accept="image/*"
        className="hidden"
        onChange={handleFileSelect}
        ref={fileInputRef}
      />
      <button
        className="bg-blue-500 hover:bg-blue-700 text-white font-bold items-center w-36 py-2 px-4 rounded mt-4"
        onClick={() => fileInputRef.current.click()}
      >
        Upload Image
      </button>
    </div>
  );
}

export default MainBoard;