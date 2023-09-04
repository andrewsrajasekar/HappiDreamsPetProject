import { ChevronLeftIcon, ChevronRightIcon } from '@heroicons/react/20/solid'
import { useEffect, useState } from 'react'

export default function Pagination({totalPages, onClickOfPageNumber, initialPerPageResult, totalResult, manualPageNumber}) {
    const [fromItem, setFromItem] = useState(1);
    const [toItem, setToItem] = useState(initialPerPageResult);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPage, setTotalPage] = useState(totalPages);
    const [totalData, setTotalData] = useState(totalResult);

    const isPreviousPageDisabled = (currentPage <= 1);
    const isNextPageDisabled = (currentPage >= totalPage);

    const onPageNumberChange = async (pageNumber) => {
        if(currentPage !== pageNumber){
            await onClickOfPageNumber(pageNumber, setFromItem, setToItem, setTotalPage, setTotalData);
            setCurrentPage(pageNumber);
        }
    }

    useEffect(() => {
      if(manualPageNumber !== undefined && manualPageNumber > 0){
        if(manualPageNumber > totalPages){
          onPageNumberChange(totalPages);
        }else{
          onPageNumberChange(manualPageNumber);
        }       
      }
    }, [manualPageNumber]);

    let paginationNumberLimit = 6;

    let currentClassName = "relative z-10 inline-flex items-center bg-indigo-600 px-4 py-2 text-sm font-semibold text-white focus:z-20 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600 cursor-pointer";
    let normalClassName = "relative inline-flex items-center px-4 py-2 text-sm font-semibold text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:z-20 focus:outline-offset-0 cursor-pointer";
    let dottedClassName = "relative inline-flex items-center px-4 py-2 text-sm font-semibold text-gray-700 ring-1 ring-inset ring-gray-300 focus:outline-offset-0";

    const elements = [];
    if((totalPage - currentPage) < paginationNumberLimit){
        for (let i = (totalPage - paginationNumberLimit > 0 ? totalPage - paginationNumberLimit : 0) + 1; i <= totalPage; i++) {
            let element = "";
            if(i == currentPage){
                element = <span key={i} className={currentClassName}>{i}</span>;
            }else if(i < currentPage){
                element = <span key={i} className={normalClassName} onClick={() => onPageNumberChange(i)}>{i}</span>;
            }else{
                element = <span key={i} className={normalClassName} onClick={() => onPageNumberChange(i)}>{i}</span>;
            }
            elements.push(element);
          }
    }else{
        elements.push(<span key={currentPage} className={currentClassName}>{currentPage}</span>);
        elements.push(<span key={currentPage + 1} className={normalClassName} onClick={() => onPageNumberChange(currentPage + 1)}>{currentPage + 1}</span>);
        elements.push(<span key={currentPage + 2} className={normalClassName} onClick={() => onPageNumberChange(currentPage + 2)}>{currentPage + 2}</span>);
        elements.push(<span key={"dotted"} className={dottedClassName}>...</span>);
        elements.push(<span key={totalPage - 2} className={normalClassName} onClick={() => onPageNumberChange(totalPage - 2)}>{totalPage - 2}</span>);
        elements.push(<span key={totalPage - 1} className={normalClassName} onClick={() => onPageNumberChange(totalPage - 1)}>{totalPage - 1}</span>);
        elements.push(<span key={totalPage} className={normalClassName} onClick={() => onPageNumberChange(totalPage)}>{totalPage}</span>);
    }



  return (
    <div className="flex items-center justify-between border-t border-gray-200 bg-white px-4 py-3 sm:px-6">
      <div className="flex flex-1 justify-between sm:hidden">
        <span
            onClick={isPreviousPageDisabled ? null : () => onPageNumberChange(currentPage - 1)}
          className={`relative inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 ${isPreviousPageDisabled ? "opacity-50 cursor-not-allowed" : "hover:bg-gray-50 cursor-pointer"}`}
        >
          Previous
        </span>
        <span
            onClick={isNextPageDisabled ? null : () => onPageNumberChange(currentPage + 1)}
          className={`relative ml-3 inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 ${isNextPageDisabled ? "opacity-50 cursor-not-allowed" : "hover:bg-gray-50 cursor-pointer"}`}
        >
          Next
        </span>
      </div>
      <div className="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
        <div>
          <p className="text-sm text-gray-700">
            Showing <span className="font-medium">{fromItem}</span> to <span className="font-medium">{toItem}</span> of{' '}
            <span className="font-medium">{totalData}</span> results
          </p>
        </div>
        <div>
          <nav className="isolate inline-flex -space-x-px rounded-md shadow-sm" aria-label="Pagination">
            <span
             onClick={isPreviousPageDisabled ? null : () => onPageNumberChange(currentPage - 1)}
              className={`relative inline-flex items-center rounded-l-md px-2 py-2 text-gray-400 ring-1 ring-inset ring-gray-300 ${isPreviousPageDisabled ? "opacity-50 cursor-not-allowed" : "cursor-pointer hover:bg-gray-50 focus:z-20 focus:outline-offset-0"}`}
            >
              <span className="sr-only">Previous</span>
              <ChevronLeftIcon className="h-5 w-5" aria-hidden="true" />
            </span>
            {elements}
            <span
              onClick={isNextPageDisabled ? null : () => onPageNumberChange(currentPage + 1)}
              className={`relative inline-flex items-center rounded-r-md px-2 py-2 text-gray-400 ring-1 ring-inset ring-gray-300 ${isNextPageDisabled ? "opacity-50 cursor-not-allowed" : "cursor-pointer hover:bg-gray-50 focus:z-20 focus:outline-offset-0"}`}
            >
              <span className="sr-only">Next</span>
              <ChevronRightIcon className="h-5 w-5" aria-hidden="true" />
            </span>
          </nav>
        </div>
      </div>
    </div>
  )
}
