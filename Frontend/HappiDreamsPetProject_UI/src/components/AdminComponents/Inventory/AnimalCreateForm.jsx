import { useRef, useState } from "react";
import compressAndResizeImage from "../../../utils/ImageCompressAndResizer";
import ImageThumbnail from "../ImageThumbnail";
import { IMAGEFORMAT } from "../../../utils/ImageFormat";

function AnimalCreateForm({animalName_Edit, animalDescription_Edit, image_Edit, imageUrl_Edit, isFileUpload_Edit, editMode}){
    const [isEditComponent, setIsEditComponent] = useState(editMode !== undefined ? editMode : false);
    const [animalName, setAnimalName] = useState(isEditComponent ? animalName_Edit : "");
    const [animalDescription, setAnimalDescription] = useState(isEditComponent ? animalDescription_Edit : "");
    const [file, setFile] = useState(isEditComponent ? image_Edit === undefined ? null : image_Edit : null);
    const [imageUrl, setImageUrl] = useState(isEditComponent ? imageUrl_Edit : "");
    const [isFileUpload, setIsFileUpload] = useState(isEditComponent ? isFileUpload_Edit : true);
    const fileInputRef = useRef(null);
  
    const handleFileChange = async(event) => {
      const selectedFile = event.target.files[0];
      const compressedImage = await compressAndResizeImage(selectedFile, 600, 360, IMAGEFORMAT.PNG);
      if (fileInputRef.current) {
        fileInputRef.current.value = "";
      }
      setFile({name: selectedFile.name, url: compressedImage});
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
      if(animalName.trim() === "" || animalDescription.trim() === ""){
        return false;
      }
      if(isFileUpload){
        if(file === null){
          return false;
        }
      }else{
        if(imageUrl.trim() === ""){
          return false;
        }
      }
      return true;
    }

    const handleFormSubmit = (event) => {
    event.preventDefault();

    // Process the chosen input (file or image URL) based on the user's choice
    if (isFileUpload) {
        // Handle file upload
        if (file) {
        // Upload the file
        console.log('Uploading file:', file);
        } else {
        // File not selected
        console.log('Please select a file.');
        }
    } else {
        // Handle image URL input
        if (imageUrl) {
        // Process the image URL
        console.log('Image URL:', imageUrl);
        } else {
        // Image URL not provided
        console.log('Please enter an image URL.');
        }
    }
    };

    return(
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