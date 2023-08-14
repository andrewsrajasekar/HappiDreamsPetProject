import { useState } from "react";
import Category from "../../../pages/Category";
import { CATEGORY_TYPE } from "../../../utils/CategoryTypes";
import AnimalCreateForm from "./AnimalCreateForm";
import { ArrowLeftIcon } from "@heroicons/react/20/solid";

function AnimalList({refreshParentComponent}){
    const [editComponent, setEditComponent] = useState(false);
    const [animalId, setAnimalId] = useState(-1);
    const [animalName, setAnimalName] = useState("");
    const [animalDescription, setAnimalDescription] = useState("");
    const [file, setFile] = useState(null);
    const [imageUrl, setImageUrl] = useState("");
    const [isFileUpload, setIsFileUpload] = useState(true);
    const [createFormKey, setKeyFormKey] = useState(1);
    const [editFormKey, setEditFormKey] = useState(1);

    const onEdit = (data) => {
        setAnimalId(data.id);
        setAnimalName(data.name);
        setAnimalDescription(data.description);
        setIsFileUpload(!data.isExternalUpload);
        if(data.isExternalUpload){
            setImageUrl(data.imageUrl);
        }else{
            setFile(data.image);
        }
        setKeyFormKey(createFormKey + 1);
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
            <Category key={editFormKey} categoryType={CATEGORY_TYPE.ANIMAL_CATEGORY} isAdminPanelUsage={true} onEdit={onEdit} onDelete={onDelete} />
            :
            <div className="flex flex-row">
            <div className="flex items-center justify-center">
            <ArrowLeftIcon className="w-12 h-12 cursor-pointer" onClick={backToList} />
            </div>
            <div className="mx-96 w-full">
                <AnimalCreateForm key={createFormKey} animalName_Edit={animalName} animalId_Edit={animalId} animalDescription_Edit={animalDescription} image_Edit={file} imageUrl_Edit={imageUrl} isFileUpload_Edit={isFileUpload} editMode={true} onEditDone={backToList} refreshParentComponent={refreshParentComponent} />
            </div>
            </div>
            
        }
       </>
    );
}

export default AnimalList;