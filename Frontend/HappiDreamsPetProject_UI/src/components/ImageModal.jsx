import { useState } from "react";
import { MagnifyingGlassPlusIcon, MagnifyingGlassMinusIcon, XMarkIcon } from "@heroicons/react/20/solid";

function ImageModal({ image, onClose }) {
  const [scale, setScale] = useState(1);
  const [isImageZoomed, setIsImageZoomed] = useState(false);
  const [xOffset, setXOffset] = useState(0);
  const [yOffset, setYOffset] = useState(0);
  const [position, setPosition] = useState({ x: 0, y: 0 });

  const handleMouseMove = (event) => {
    const { clientX, clientY } = event;
    setPosition({
      x: (clientX - event.target.offsetLeft) / scale,
      y: (clientY - event.target.offsetTop) / scale,
    });
  };

  const handleMouseLeave = () => {
    setXOffset(0);
    setYOffset(0);
  };

  function handleClose() {
    setScale(1);
    onClose();
  }

  const handleZoom = (e) => {
    if(["closeButton", "closeButtonIcon"].includes(e.target.id)){
      return;
    }
    e.preventDefault();
    const delta = Math.sign(e.deltaY);
    if(isImageZoomed){
      setScale((prevScale) => {
        const newScale = prevScale -  0.5;
        return Math.max(newScale, 1);
      });
    }else{
      setScale((prevScale) => {
        const newScale = prevScale +  0.5;
        return Math.max(newScale, 1);
      });
    }
    setIsImageZoomed(!isImageZoomed);
  }

  const styles = {
    transform: `scale(${scale})`,
    backgroundPosition: `${position.x}px ${position.y}px`,
  };

  return (
    <div
    className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
  >
    <div
      className={`relative w-96 h-96 overflow-hidden ${isImageZoomed ? "cursor-zoom-out" : "cursor-zoom-in"}`}
      onMouseMove={handleMouseMove}
      onMouseLeave={handleMouseLeave}
      onClick={handleZoom}
      
    >
      <div style={styles}> <img
        src={image}
        alt=""
        className="w-full h-full object-contain"  
      /></div>
     
      <div className="absolute top-0 right-0 m-2">
        <button className="bg-white rounded-full p-2" id="closeButton"  onClick={handleClose}>
          <XMarkIcon className="h-6 w-6 text-gray-700" id="closeButtonIcon" />
        </button>
      </div>
    </div>
  </div>
  );
}

export default ImageModal;