let fixedInputClass="rounded-md appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-purple-500 focus:border-purple-500 focus:z-10 sm:text-sm"

export default function LoginInput({
    handleChange,
    handleKeyPress,
    handleBlur,
    value,
    labelText,
    labelFor,
    id,
    name,
    type,
    minLength,
    maxLength,
    isRequired=false,
    placeholder,
    customClass,
    errors
}){

  if(errors && errors[id]){
    if(!customClass){
      customClass = "";
    }
    customClass += " border-red-500";
  }
    return(
        <div className="my-5 sm:my-3">
            <label htmlFor={labelFor} className="sr-only">
              {labelText}
            </label>
            <input
              onChange={handleChange}
              onKeyDown={handleKeyPress}
              onBlur={handleBlur}
              value={value}
              id={id}
              name={name}
              minLength={minLength}
              maxLength={maxLength}
              type={type}
              required={isRequired}
              className={customClass !== undefined ? fixedInputClass + " " + customClass : fixedInputClass}
              placeholder={placeholder}
            />
            {errors && errors[id] && <div className="error text-red-500 text-xs">{errors[id]}</div>}
          </div>
    )
}