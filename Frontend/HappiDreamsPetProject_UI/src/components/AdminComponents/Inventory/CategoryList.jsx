import { useState } from "react";
import Category from "../../../pages/Category";
import { CATEGORY_TYPE } from "../../../utils/CategoryTypes";
import CategoryCreateForm from "./CategoryCreateForm";
import { ArrowLeftIcon } from "@heroicons/react/20/solid";

function CategoryList({animalId, refreshParentComponent}){
    const [editComponent, setEditComponent] = useState(false);
    const [categoryId, setCategoryId] = useState(-1);
    const [categoryName, setCategoryName] = useState("");
    const [categoryDescription, setCategoryDescription] = useState("");
    const [file, setFile] = useState(null);
    const [imageUrl, setImageUrl] = useState("");
    const [isFileUpload, setIsFileUpload] = useState(true);
    const [createFormKey, setCreateFormKey] = useState(1);
    const [editFormKey, setEditFormKey] = useState(1);

    const onEdit = (data) => {
        setCategoryId(data.id);
        setCategoryName(data.name);
        setCategoryDescription(data.description);
        setIsFileUpload(!data.isExternalUpload);
        if(data.isExternalUpload){
            setImageUrl(data.imageUrl);
        }else{
            setFile(data.image);
        }
        setCreateFormKey(createFormKey + 1);
        setEditComponent(true);
    }

    const onDelete = () => {
        if(refreshParentComponent && typeof refreshParentComponent === "function"){
            refreshParentComponent();
        }
    }

    const backToList = () => {
        setEditComponent(false);
        setEditFormKey(editFormKey + 1);
    }

    return (
        <>
        {!editComponent ?
            <Category key={editFormKey} animalId_AdminPanel={animalId} categoryType={CATEGORY_TYPE.ANIMAL_PRODUCT_CATEGORY} isAdminPanelUsage={true} onEdit={onEdit} onDelete={onDelete}  />
            :
            <div className="flex flex-row">
            <div className="flex items-center justify-center">
            <ArrowLeftIcon className="w-12 h-12 cursor-pointer" onClick={backToList} />
            </div>
            <div className="mx-96 w-full">
                <CategoryCreateForm animalId={animalId} categoryId_Edit={categoryId} key={createFormKey} categoryName_Edit={categoryName} categoryDescription_Edit={categoryDescription} image_Edit={file} imageUrl_Edit={imageUrl} isFileUpload_Edit={isFileUpload} editMode={true}  onEditDone={backToList} refreshParentComponent={refreshParentComponent} />
            </div>
            </div>
            
        }
       </>
    );
}

export default CategoryList;