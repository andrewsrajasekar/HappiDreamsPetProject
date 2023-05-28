import { useEffect, useRef, useState } from "react";
import ImageThumbnail from "../ImageThumbnail";
import Modal from "../../Modal";
import MainPageCarousel from "../../MainPageCarousel";
import React from "react";
import ReactTooltip from 'react-tooltip'

function MainBoard(){
    const imagesArray = [
        {
            "id": 1,
            "url": "https://dummyimage.com/1203x503"
        },
        {
            "id": 2,
            "url": "https://dummyimage.com/1203x503"
        },
        {
            "id": 3,
            "url": "https://dummyimage.com/1203x503"
        }
    ];
    const [nextImageId, setNextImageId] = useState(4);
    const [images, setImages] = useState(imagesArray);
    const [showPreviewModal, setShowPreviewModal] = useState(false);
    const maxNumberOfImages = 5;

    useEffect(() => {
      if(images.length >= 5){
        ReactTooltip.rebuild();
      }
    }, [images.length])
  
    const handleDeleteImage = (imageId) => {
      setImages(images.filter((image) => image.id !== imageId));
    };

    useEffect(() => {
      ReactTooltip.rebuild();
    }, []);
 
    const handleImageUpload = (newImage) => {
      // Perform image upload and store logic here
      // Example: save the image to the state
      const image = {
        id: nextImageId,
        name: newImage.name,
        url: URL.createObjectURL(newImage),
      };
      setImages((prevImages) => [...prevImages, image]);
      setNextImageId(nextImageId+1);
    };
  
    const handleSaveProgress = () => {
      // Save progress logic here
      console.log('Progress saved!');
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
          className="bg-blue-500 w-36 hover:bg-blue-700 items-start text-white font-bold py-2 px-4 rounded mt-4"
          onClick={handleTogglePreviewModal}
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
        <button
          className="bg-green-500 w-36 hover:bg-green-700 text-white font-bold py-2 px-4 rounded mt-4"
          onClick={handleSaveProgress}
        >
          Save
        </button>
        </div>
      </div>
    );

}

function ImageUploader({ onImageUpload }) {
  const fileInputRef = useRef(null);

  const handleFileSelect = (event) => {
    const file = event.target.files[0];
    onImageUpload(file);
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