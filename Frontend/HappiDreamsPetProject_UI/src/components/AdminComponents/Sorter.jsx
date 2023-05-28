import { useState } from 'react';

const Sorter = ({ data, handleSaveFunction }) => {
  const [sortedData, setSortedData] = useState(data);

  const handleDragStart = (event, index) => {
    event.dataTransfer.setData('text/plain', index);
  };

  const handleDragOver = (event) => {
    event.preventDefault();
  };

  const handleDrop = (event, newIndex) => {
    event.preventDefault();
    const oldIndex = event.dataTransfer.getData('text/plain');
    const updatedData = arrayMove(sortedData, oldIndex, newIndex);
    setSortedData(updatedData);
  };

  const arrayMove = (arr, oldIndex, newIndex) => {
    const updatedArray = [...arr];
    updatedArray.splice(newIndex, 0, updatedArray.splice(oldIndex, 1)[0]);
    return updatedArray;
  };

  const handleSave = () => {
    if(handleSaveFunction !== undefined){
        handleSaveFunction(sortedData);
    }
  };

  return (
    <div className="p-4">
      <h2 className="text-2xl font-bold mb-4">Category Sorter</h2>

      <ul className="space-y-2">
        {sortedData.map((category, index) => (
          <li
            key={index}
            className="bg-gray-200 p-2 rounded cursor-move"
            draggable
            onDragStart={(e) => handleDragStart(e, index)}
            onDragOver={handleDragOver}
            onDrop={(e) => handleDrop(e, index)}
          >
            {category.label}
          </li>
        ))}
      </ul>

      <button
        className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded mt-4"
        onClick={handleSave}
      >
        Save Sorted Data
      </button>
    </div>
  );
};

export default Sorter;
