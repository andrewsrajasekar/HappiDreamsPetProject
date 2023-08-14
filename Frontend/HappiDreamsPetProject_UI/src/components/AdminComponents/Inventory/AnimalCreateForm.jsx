import { useRef, useState } from "react";
import compressAndResizeImage from "../../../utils/ImageCompressAndResizer";
import ImageThumbnail from "../ImageThumbnail";
import { IMAGEFORMAT } from "../../../utils/ImageFormat";
import { addImageFileToAnimal, addImageUrlToAnimal, createAnimal, deleteImageFromAnimal, updateAnimal } from "../../../services/ApiClient";
import UINotification from '../../UINotification';

function AnimalCreateForm({ animalId_Edit, animalName_Edit, animalDescription_Edit, image_Edit, imageUrl_Edit, isFileUpload_Edit, editMode, onEditDone, onCreateDone, refreshParentComponent }) {
  const [isEditComponent, setIsEditComponent] = useState(editMode !== undefined ? editMode : false);
  const [animalName, setAnimalName] = useState(isEditComponent ? animalName_Edit : "");
  const [animalDescription, setAnimalDescription] = useState(isEditComponent ? animalDescription_Edit : "");
  const [file, setFile] = useState(isEditComponent ? image_Edit === undefined ? null : image_Edit : null);
  const [imageUrl, setImageUrl] = useState(isEditComponent ? imageUrl_Edit : "");
  const [isFileUpload, setIsFileUpload] = useState(isEditComponent ? isFileUpload_Edit : true);
  const fileInputRef = useRef(null);

  const handleFileChange = async (event) => {
    const selectedFile = event.target.files[0];
    const compressedImage = await compressAndResizeImage(selectedFile, 600, 360, IMAGEFORMAT.PNG);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
    setFile({ name: selectedFile.name, url: compressedImage });
  };

  const handleDeleteImage = () => {
    setFile(null);
  }

  const handleImageUrlChange = (event) => {
    const url = event.target.value;
    setImageUrl(url);
  };

  const handleInputChange = () => {
    setIsFileUpload(!isFileUpload);
    setFile(null);
    setImageUrl('');
  };

  const isSaveEnabled = () => {
    if (animalName.trim() === "" || animalDescription.trim() === "") {
      return false;
    }
    if (isFileUpload) {
      if (file === null) {
        return false;
      }
    } else {
      if (imageUrl.trim() === "") {
        return false;
      }
    }
    return true;
  }

  const handleFormSubmit = async (event) => {
    event.preventDefault();
    if (isEditComponent) {
      const updateAnimalResponse = await updateAnimal(animalName.trim(), animalDescription.trim(), animalId_Edit);
      if (updateAnimalResponse.isSuccess) {
        if(refreshParentComponent && typeof refreshParentComponent === "function"){
          refreshParentComponent();
        }
        const deleteImageResponse = await deleteImageFromAnimal(animalId_Edit);
        if(deleteImageResponse.statusCode === 404 && deleteImageResponse.failureResponse.errors && deleteImageResponse.failureResponse.errors.field && deleteImageResponse.failureResponse.errors.field === "image"){
          deleteImageResponse.isSuccess = true;
        }
        if (deleteImageResponse.isSuccess) {
          if (isFileUpload) {
            const uploadImageResponse = await addImageFileToAnimal(animalId_Edit, file);
            if (uploadImageResponse.isSuccess) {
              UINotification({ message: "Animal Data Updated", type: "Success" });
              setAnimalName("");
              setAnimalDescription("");
              setIsFileUpload(true);
              setImageUrl("");
              setFile(null);
              if(onEditDone && typeof onEditDone === "function"){
                onEditDone();
              }
            } else {
              UINotification({ message: "Issue Occured, while adding the image", type: "Error" });
            }
          } else {
            const addImageUrlResponse = await addImageUrlToAnimal(animalId_Edit, imageUrl);
            if (addImageUrlResponse.isSuccess) {
              UINotification({ message: "Animal Data Updated", type: "Success" });
              setAnimalName("");
              setAnimalDescription("");
              setIsFileUpload(true);
              setImageUrl("");
              setFile(null);
              if(onEditDone && typeof onEditDone === "function"){
                onEditDone();
              }
            } else {
              UINotification({ message: "Issue Occured, while adding the image URL", type: "Error" });
            }
          }

        } else {
          UINotification({ message: "Issue Occured While saving the image, Kindly try again later.", type: "Error" });
        }
      } else {
        UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
      }
    } else {
      const createAnimalResponse = await createAnimal(animalName.trim(), animalDescription.trim());
      if (createAnimalResponse.isSuccess) {
        if (isFileUpload) {
          const uploadImageResponse = await addImageFileToAnimal(createAnimalResponse.successResponse.data.data.id, file);
          if (uploadImageResponse.isSuccess) {
            UINotification({ message: "Animal Data Added", type: "Success" });
          } else {
            UINotification({ message: "Issue Occured, while adding the image, but the animal is saved, Kindly go to Edit animal and add Image", type: "Error" });
          }
        } else {
          const addImageUrlResponse = await addImageUrlToAnimal(createAnimalResponse.successResponse.data.data.id, imageUrl);
          if (addImageUrlResponse.isSuccess) {
            UINotification({ message: "Animal Data Added", type: "Success" });
          } else {
            UINotification({ message: "Issue Occured, while adding the image URL, but the animal is saved, Kindly go to Edit animal and add Image URL", type: "Error" });
          }
        }
        setAnimalName("");
        setAnimalDescription("");
        setIsFileUpload(true);
        setImageUrl("");
        setFile(null);
        if(onCreateDone && typeof onCreateDone === "function"){
          onCreateDone();
        }
      } else {
        UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
      }
    }
  };

  return (
    <>
      <h3 className="font-bold text-lg flex items-center justify-center mb-5" id="titleText">{isEditComponent ? "Update" : "Add"} Animal</h3>
      <form onSubmit={handleFormSubmit} className="mt-8">
        <div className="mb-4">
          <label className="block text-gray-700 font-bold mb-2" htmlFor="animalName">
            Animal Name
          </label>
          <input
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="animalName"
            type="text"
            placeholder="Enter animal Name"
            minLength={3}
            maxLength={50}
            value={animalName}
            onChange={(e) => setAnimalName(e.target.value)}
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700 font-bold mb-2" htmlFor="animalDescription">
            Animal Description
          </label>
          <textarea
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="animalName"
            type="text"
            placeholder="Enter animal Description"
            minLength={3}
            maxLength={256}
            value={animalDescription}
            onChange={(e) => setAnimalDescription(e.target.value)}
          />
        </div>
        <div className="mb-4">
          <label className="block mb-2 text-sm font-bold text-gray-700" htmlFor="inputChoice">
            Choose Input Type
          </label>
          <select
            className="border border-gray-300 px-3 py-2 rounded-lg w-full"
            id="inputChoice"
            value={isFileUpload ? 'file' : 'url'}
            onChange={handleInputChange}
          >
            <option value="file">Upload File</option>
            <option value="url">Image URL</option>
          </select>
        </div>
        {isFileUpload ? (
          <div className="mb-4">
            <label className="block mb-2 text-sm font-bold text-gray-700" htmlFor="fileInput">
              Upload File
            </label>
            <input
              className="border border-gray-300 px-3 py-2 rounded-lg w-full"
              type="file"
              id="fileInput"
              accept="image/*"
              disabled={file !== null}
              onChange={file !== null ? null : handleFileChange}
              ref={fileInputRef}
            />
            {file !== null &&
              <div className="flex items-center justify-center flex-row mt-5">
                <div>
                  <ImageThumbnail
                    image={file}
                    onDelete={handleDeleteImage}
                    showName={false}
                    removeWidth={true}
                  />
                </div>
              </div>
            }
          </div>
        ) : (
          <div className="mb-4">
            <label className="block mb-2 text-sm font-bold text-gray-700" htmlFor="imageUrlInput">
              Image URL
            </label>
            <input
              className="border border-gray-300 px-3 py-2 rounded-lg w-full"
              type="text"
              id="imageUrlInput"
              value={imageUrl}
              onChange={handleImageUrlChange}
            />
          </div>
        )}
        <button
          className="bg-purple-600 hover:bg-purple-900 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline disabled:opacity-25 disabled:cursor-not-allowed"
          type="submit"
          disabled={!isSaveEnabled()}
        >
          {isEditComponent ? "Update" : "Save"}
        </button>
      </form>
    </>
  )
}

export default AnimalCreateForm;