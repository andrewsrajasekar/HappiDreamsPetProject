import { useState, useEffect } from "react";
import { CheckCircleIcon } from "@heroicons/react/20/solid";
import { useNavigate  } from "react-router-dom";
import { motion } from "framer-motion";

function OrderConfirmationPage() {
  const [animate, setAnimate] = useState(false);
  const navigate = useNavigate();

  const variants = {
  initial: { opacity: 0 },
  animate: {
    opacity: 1,
    transition: { duration: 1, ease: "easeInOut" },
  },
  exit: {
    opacity: 0,
    transition: { duration: 2, ease: "easeInOut" },
  },
};

  useEffect(() => {

    const timer = setTimeout(() => {
      setAnimate(true);
    }, 500);

    return () => clearTimeout(timer);
  }, []);

  const handleGoToHome = () => {
    navigate("/");
  };

  return (
    <motion.div
      className={`flex flex-col items-center justify-center mt-20`}
      initial="initial"
      animate="animate"
      exit="exit"
      variants={variants}
    >
      <div
        className={`${
          animate ? "animate-bounce" : ""
        } flex items-center justify-center w-20 p-2 mb-8 rounded-full bg-green-500`}
      >
        <CheckCircleIcon className={` w-full h-full text-white`} />
      </div>
      <h1 className="mb-4 text-2xl font-bold text-gray-800">Order Confirmed!</h1>
      <p className="text-base text-gray-600">
        Thank you for your purchase.
      </p>
      <p className="mb-8 text-base text-gray-600">
      Our Administrator will contact you regarding Payment and Next Steps!
      </p>
      <button
        onClick={handleGoToHome}
        className="px-4 py-2 text-white bg-green-500 rounded hover:bg-green-900 focus:outline-none focus:ring-2 focus:ring-green-600 focus:ring-opacity-50 cursor-pointer"
      >
        Go to Home
      </button>
    </motion.div>
  );
}

export default OrderConfirmationPage;
