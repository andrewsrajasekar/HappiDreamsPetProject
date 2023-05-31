import { useEffect, useRef, useState } from "react";
import ImageThumbnail from "../ImageThumbnail";
import Modal from "../../Modal";
import MainPageCarousel from "../../MainPageCarousel";
import React from "react";
import ReactTooltip from 'react-tooltip'

function AboutUs(){
    const imagesArray = [
        {
            "id": 1,
            "url": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo.jpg?v=1668421991",
            "name" : "Fiedele"
        },
        {
            "id": 2,
            "url": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo1.jpg?v=1668422020",
            "name" : "Applaws"
        },
        {
            "id": 3,
            "url": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo2.jpg?v=1668422062",
            "name" : "Arden Grange"
        },
        {
            "id": 4,
            "url": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo3.jpg?v=1668422091",
            "name" : "Fish4Dogs"
        },
        {
            "id": 5,
            "url": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo4.jpg?v=1668422195",
            "name" : "Kit Cat"
        },
        {
            "id": 6,
            "url": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo5.jpg?v=1668422240",
            "name" : "Lara"
        },
        {
            "id": 7,
            "url": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo6.jpg?v=1668422266",
            "name" : "Mera"
        },
        {
            "id": 8,
            "url": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo7.jpg?v=1668422292",
            "name" : "mybeau"
        }
    ];
    const [nextImageId, setNextImageId] = useState(4);
    const [images, setImages] = useState(imagesArray);
    const [showPreviewModal, setShowPreviewModal] = useState(false);
    const [modalContent, setModalContent] = useState();
    const maxNumberOfImages = 10;

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
        if(!showPreviewModal){
            setModalContent(
                <div className="w-full mt-10 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="mt-6 grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 flex items-center">
                {images.map((company, index) => (
                  <div key={company.id} className={`col-span-1 flex justify-center py-8 border-solid border-black ${index % 4 === 0 ? (index === 0 ? 'border-2' : 'border-l-2 border-r-2 border-b-2') : (Math.floor(index / 4) > 0 ? 'border-r-2 border-b-2' : 'border-t-2 border-r-2 border-b-2' )}`}>
                    <img
                      className="max-h-12"
                      src={company.url}
                      alt={company.name}
                    />
                  </div>
                ))}
              </div>
              </div>
              );
        }
      setShowPreviewModal(!showPreviewModal);
    };

    useEffect(() => {
        if(showPreviewModal){
            document.body.classList.toggle("overflow-hidden");
        }else{
            document.body.classList.remove("overflow-hidden"); 
        }
    }, [showPreviewModal])
  
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
            content={modalContent}
            onClose={handleTogglePreviewModal}
            isZoomEnabled={false}
            bgColorClass={"bg-white"}
            preventPredefinedDiv={true}
            width={`80%`}
            height={`80%`}
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

export default AboutUs;