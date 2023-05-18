module.exports = {
    "env": {
        "browser": true,
        "es2021": true
    },
    "extends": [
        "eslint:recommended",
        "plugin:react/recommended",
        "nicenice"
    ],
    "overrides": [
    ],
    "parserOptions": {
        "ecmaVersion": "latest",
        "sourceType": "module"
    },
    "plugins": [
        "react",
        "react-camel-case"
    ],
    "rules": {
      'react-camel-case/react-camel-case': 'error'
    },
    "settings": {
        "react": {
          "version": "18.2.0"
        }
      }
}
