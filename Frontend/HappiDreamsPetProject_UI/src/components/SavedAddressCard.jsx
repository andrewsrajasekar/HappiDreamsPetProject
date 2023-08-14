import React from "react";

const SavedAddressCard = ({ address_id, address, city, state, country, pinCode, selected, onSelect, isDefaultNeedsToBeGiven, isEditShown, isDeleteShown, handleEdit, handleDelete }) => {
  return (
    <div className="bg-white rounded-lg shadow-lg p-6 mb-4">
      <p className="text-gray-700 font-bold mb-2">{address}</p>
      <p className="text-gray-700 mb-2">
        {city}, {state} {pinCode}, {country}
      </p>

      <div className="flex-row items-start justify-center mt-1">
        {!selected && <button
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          onClick={onSelect}
        >
          {isDefaultNeedsToBeGiven ? "Select as Default" : "Select"}
        </button>}
        {selected &&
          <button
            className="opacity-25 cursor-not-allowed bg-gray-500 font-bold py-2 px-4 rounded"
          >
            Selected
          </button>
        }
        {
          isEditShown && typeof handleEdit === "function" &&
          <span className="ml-4 cursor-pointer text-indigo-500 hover:text-indigo-900" onClick={() => { handleEdit({ address_id, address, city, state, country, pinCode, selected }) }}>Edit</span>
        }
        {
          isDeleteShown && typeof handleDelete === "function" &&
          <span className="ml-4 cursor-pointer text-red-500 hover:text-red-900" onClick={() => { handleDelete({ address_id, address, city, state, country, pinCode, selected }) }}>Delete</span>
        }
      </div>

    </div>
  );
};

export default SavedAddressCard;
