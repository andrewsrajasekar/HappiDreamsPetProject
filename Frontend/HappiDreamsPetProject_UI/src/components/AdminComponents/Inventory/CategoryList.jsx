import { useState } from "react";
import Category from "../../../pages/Category";
import { CATEGORY_TYPE } from "../../../utils/CategoryTypes";
import CategoryCreateForm from "./CategoryCreateForm";
import { ArrowLeftIcon } from "@heroicons/react/20/solid";

function CategoryList(){
    const [editComponent, setEditComponent] = useState(false);
    const [categoryName, setCategoryName] = useState("");
    const [categoryDescription, setCategoryDescription] = useState("");
    const [file, setFile] = useState(null);
    const [imageUrl, setImageUrl] = useState("");
    const [isFileUpload, setIsFileUpload] = useState(true);
    const [createFormKey, setKeyFormKey] = useState(1);

    const onEdit = (data) => {
        setCategoryName(data.name);
        setCategoryDescription(data.description);
        setIsFileUpload(!data.isExternalUpload);
        if(data.isExternalUpload){
            setImageUrl(data.imageUrl);
        }else{
            setFile(data.image);
        }
        setKeyFormKey(createFormKey + 1);
        setEditComponent(true);
    }

    const backToList = () => {
        setEditComponent(false);
    }

    return (
        <>
        {!editComponent ?
            <Category key={CATEGORY_TYPE.ANIMAL_PRODUCT_CATEGORY} categoryType={CATEGORY_TYPE.ANIMAL_PRODUCT_CATEGORY} isAdminPanelUsage={true} onEdit={onEdit} />
            :
            <div className="flex flex-row">
            <div className="flex items-center justify-center">
            <ArrowLeftIcon className="w-12 h-12 cursor-pointer" onClick={backToList} />
            </div>
            <div className="mx-96 w-full">
                <CategoryCreateForm key={createFormKey} categoryName_Edit={categoryName} categoryDescription_Edit={categoryDescription} image_Edit={file} imageUrl_Edit={imageUrl} isFileUpload_Edit={isFileUpload} editMode={true} />
            </div>
            </div>
            
        }
       </>
    );
}

export default CategoryList;