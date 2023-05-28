import { useState } from "react";

function AnimalCreateForm(){
    const [animalName, setAnimalName] = useState("");
    const [animalDescription, setAnimalDescription] = useState("");
    const [file, setFile] = useState(null);
    const [imageUrl, setImageUrl] = useState('');
    const [isFileUpload, setIsFileUpload] = useState(true);
  
    const handleFileChange = (event) => {
      const selectedFile = event.target.files[0];
      setFile(selectedFile);
    };
  
    const handleImageUrlChange = (event) => {
      const url = event.target.value;
      setImageUrl(url);
    };

    const handleInputChange = () => {
        setIsFileUpload(!isFileUpload);
        setFile(null);
        setImageUrl('');
    };

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
                <input
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
            onChange={handleFileChange}
          />
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
        className="bg-purple-600 hover:bg-purple-900 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="submit"
      >
        Save
      </button>
        </form>
        </>
    )
}

export default AnimalCreateForm;