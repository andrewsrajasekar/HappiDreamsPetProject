const fixedInputClass="rounded-md appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-purple-500 focus:border-purple-500 focus:z-10 sm:text-sm"

export default function LoginInput({
    handleChange,
    handleKeyPress,
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
    customClass
}){


    return(
        <div className="my-5 sm:my-3">
            <label htmlFor={labelFor} className="sr-only">
              {labelText}
            </label>
            <input
              onChange={handleChange}
              onKeyDown={handleKeyPress}
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
          </div>
    )
}