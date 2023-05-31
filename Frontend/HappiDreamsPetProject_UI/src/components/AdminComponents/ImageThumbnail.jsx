import React from 'react';

function ImageThumbnail({ image, onDelete, showName, removeWidth }) {
  const handleDelete = () => {
    onDelete(image.id);
  };

  const getImageSizeClass = () => {
    // Determine the size of the image
    if (image.width < 200 && image.height < 200) {
      return 'max-h-36';
    } else if (image.width < 400 && image.height < 400) {
      return 'max-h-48';
    } else {
      return 'max-h-60';
    }
  };

  return (
    <div className={`${removeWidth !== undefined ? removeWidth ? "" : "w-1/4" : "w-1/4"} p-2`}>
      <div className="relative">
        <img
          src={image.url}
          alt={image.name}
          className={`w-full max-h-36 rounded ${getImageSizeClass()}`}
        />
        {onDelete !== undefined && <button
          className={`absolute top-2 right-2 bg-red-500 hover:bg-red-700 text-white font-bold text-xs px-2 py-1 rounded ${getImageSizeClass()}`}
          onClick={onDelete !== undefined ? handleDelete: null}
        >
          Delete
        </button>}
      </div>
      {showName && <p className="text-center mt-2">{image.name}</p>}
    </div>
  );
}

export default ImageThumbnail;
