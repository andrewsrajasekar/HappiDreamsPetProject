import React from "react";

const SavedAddressCard = ({ address, city, state, country, zipCode, selected, onSelect, isDefaultNeedsToBeGiven }) => {
  return (
    <div className="bg-white rounded-lg shadow-lg p-6 mb-4">
      <p className="text-gray-700 font-bold mb-2">{address}</p>
      <p className="text-gray-700 mb-2">
        {city}, {state} {zipCode}, {country}
      </p>
      {!selected &&  <button
        className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        onClick={onSelect}
      >
        {isDefaultNeedsToBeGiven ? "Select as Default" : "Select"}
      </button>}
      {selected && 
      <button
      className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
    >
      Selected
    </button>
      }
    </div>
  );
};

export default SavedAddressCard;
