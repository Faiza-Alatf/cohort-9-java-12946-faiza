import axios from "axios";
import api from "./api";

const getAuthError = (error, defaultMessage) => {
if (axios.isAxiosError(error)) {
if (error.code === "ECONNABORTED") {
return new Error(
"The request timed out. Please try again."
);
}


if (error.response?.data?.error) {
  return new Error(
    error.response.data.error
  );
}

if (error.response?.status === 401) {
  return new Error(
    "Invalid email/phone or password."
  );
}

if (error.response?.status === 409) {
  return new Error(
    "Email or phone number is already registered."
  );
}


}

return new Error(defaultMessage);
};

export const registerUser = async (userData) => {
try {
const response = await api.post(
"/auth/register",
userData
);


return response.data;


} catch (error) {
throw getAuthError(
error,
"Registration failed. Please try again."
);
}
};

export const loginUser = async (loginData) => {
try {
const response = await api.post(
"/auth/login",
loginData
);


return response.data;


} catch (error) {
throw getAuthError(
error,
"Login failed. Please check your credentials."
);
}
};
