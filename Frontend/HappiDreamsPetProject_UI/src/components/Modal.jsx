import { useState } from "react";
import { XMarkIcon } from "@heroicons/react/20/solid";

function Modal({ content, onClose, width, height, isZoomEnabled, preventPredefinedDiv, bgColorClass }) {
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
      id="predefinedModal"
      className={`relative overflow-hidden ${bgColorClass !== undefined ? bgColorClass : ""} ${isZoomEnabled ? isImageZoomed ? "cursor-zoom-out" : "cursor-zoom-in" : "cursor-default"}`}
      onMouseMove={isZoomEnabled ? handleMouseMove : null}
      onMouseLeave={isZoomEnabled ? handleMouseLeave : null}
      onClick={isZoomEnabled ? handleZoom : null}
      style={{width: width !== undefined ? width : "100%", height: height !== undefined ? height : "100%"}}
    >
      {preventPredefinedDiv !== undefined && preventPredefinedDiv && 
        content
      }
      {
        preventPredefinedDiv == undefined && 
        <div style={styles}> 
        {content}
        </div>
      }
      {
        preventPredefinedDiv !== undefined && !preventPredefinedDiv && 
        <div style={styles}> 
        {content}
        </div>
      }

     
      <div className="absolute top-0 right-0 m-2">
        <button className="bg-white rounded-full p-2" id="closeButton"  onClick={handleClose}>
          <XMarkIcon className="h-6 w-6 text-gray-700" id="closeButtonIcon" />
        </button>
      </div>
    </div>
  </div>
  );
}

export default Modal;