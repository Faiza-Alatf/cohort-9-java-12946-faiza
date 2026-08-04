
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";


function Register() {


  const navigate = useNavigate();



  const [formData,setFormData] = useState({

    firstName:"",
    lastName:"",
    email:"",
    phone:"",
    password:""

  });



  const [error,setError] = useState("");

  const [loading,setLoading] = useState(false);






  const handleChange = (e)=>{


    setFormData({

      ...formData,

      [e.target.name]:
      e.target.value

    });


  };






  const handleSubmit = async(e)=>{


    e.preventDefault();


    setError("");

    setLoading(true);




    try {



      const response =
      await api.post(
        "/auth/register",
        formData
      );





      localStorage.setItem(

        "token",

        response.data.token

      );





      const {
        token,
        ...userData

      } = response.data;





      localStorage.setItem(

        "user",

        JSON.stringify(userData)

      );





      navigate("/dashboard");





    }
    catch(err){


      setError(

        err.response?.data?.error ||

        "Registration failed. Please try again."

      );


    }
    finally{


      setLoading(false);


    }


  };







  return (



    <div className="auth-container">





      <div className="auth-card register-card">





        <div className="auth-header">



          <div className="auth-logo">

            CM

          </div>




          <h1>
            Create Account
          </h1>




          <p className="auth-subtitle">

            Join Contact Management System

          </p>



        </div>









        {
          error &&

          <div className="error-message">

            {error}

          </div>
        }










        <form onSubmit={handleSubmit}>






          <div className="form-row">



            <div className="form-group">


              <label>
                First Name
              </label>



              <input

                type="text"

                name="firstName"

                value={formData.firstName}

                onChange={handleChange}

                placeholder="First name"

                autoComplete="given-name"

                required

              />


            </div>







            <div className="form-group">


              <label>
                Last Name
              </label>



              <input

                type="text"

                name="lastName"

                value={formData.lastName}

                onChange={handleChange}

                placeholder="Last name"

                autoComplete="family-name"

                required

              />


            </div>



          </div>









          <div className="form-group">


            <label>
              Email
            </label>



            <input

              type="email"

              name="email"

              value={formData.email}

              onChange={handleChange}

              placeholder="Enter your email"

              autoComplete="email"

              required

            />


          </div>









          <div className="form-group">


            <label>
              Phone Number
            </label>



            <input

              type="text"

              name="phone"

              value={formData.phone}

              onChange={handleChange}

              placeholder="Enter phone number"

              autoComplete="tel"

            />


          </div>









          <div className="form-group">


            <label>
              Password
            </label>



            <input

              type="password"

              name="password"

              value={formData.password}

              onChange={handleChange}

              placeholder="Create a strong password"

              autoComplete="new-password"

              required

            />


          </div>









          <button

            type="submit"

            className="auth-button"

            disabled={loading}

          >



            {

              loading

              ?

              "Creating Account..."

              :

              "Register"

            }



          </button>





        </form>









        <p className="auth-footer">


          Already have an account?


          {" "}



          <Link to="/login">

            Login here

          </Link>



        </p>








      </div>





    </div>



  );


}



export default Register;

