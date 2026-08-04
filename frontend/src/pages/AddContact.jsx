
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";


function AddContact() {


  const navigate = useNavigate();



  const [formData,setFormData] = useState({

    firstName:"",
    lastName:"",
    title:"",
    workEmail:"",
    personalEmail:"",
    workPhone:"",
    homePhone:"",
    personalPhone:""

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


      await api.post(
        "/contacts",
        formData
      );



      navigate("/dashboard");



    }
    catch(err){



      if(err.response?.status === 401){


        localStorage.removeItem("token");

        localStorage.removeItem("user");


        navigate("/login");

        return;

      }




      setError(

        err.response?.data?.error ||

        "Unable to create contact."

      );



    }
    finally{


      setLoading(false);


    }


  };








  return (



    <div className="auth-container">





      <div className="auth-card contact-form-card">





        <button

          className="profile-back-button"

          onClick={()=>
            navigate("/dashboard")
          }

        >

          ← Back to Contacts

        </button>







        <div className="auth-header">



          <div className="auth-logo">

            +

          </div>




          <h1>
            Add New Contact
          </h1>




          <p className="auth-subtitle">

            Create a new professional contact

          </p>



        </div>








        {error && (

          <div className="error-message">

            {error}

          </div>

        )}










        <form onSubmit={handleSubmit}>






          <h3>
            Personal Information
          </h3>





          <div className="form-row">



            <div className="form-group">

              <label>
                First Name
              </label>


              <input

                name="firstName"

                value={formData.firstName}

                onChange={handleChange}

                placeholder="First name"

                required

              />

            </div>







            <div className="form-group">

              <label>
                Last Name
              </label>


              <input

                name="lastName"

                value={formData.lastName}

                onChange={handleChange}

                placeholder="Last name"

                required

              />

            </div>



          </div>







          <div className="form-group">

            <label>
              Job Title
            </label>


            <input

              name="title"

              value={formData.title}

              onChange={handleChange}

              placeholder="Software Engineer"

            />


          </div>









          <h3>
            Email Information
          </h3>





          <div className="form-row">



            <div className="form-group">

              <label>
                Work Email
              </label>


              <input

                type="email"

                name="workEmail"

                value={formData.workEmail}

                onChange={handleChange}

                placeholder="office@email.com"

              />


            </div>






            <div className="form-group">

              <label>
                Personal Email
              </label>


              <input

                type="email"

                name="personalEmail"

                value={formData.personalEmail}

                onChange={handleChange}

                placeholder="personal@email.com"

              />


            </div>



          </div>









          <h3>
            Phone Information
          </h3>






          <div className="form-row">


            <div className="form-group">

              <label>
                Work Phone
              </label>


              <input

                name="workPhone"

                value={formData.workPhone}

                onChange={handleChange}

                placeholder="Work number"

              />


            </div>






            <div className="form-group">

              <label>
                Home Phone
              </label>


              <input

                name="homePhone"

                value={formData.homePhone}

                onChange={handleChange}

                placeholder="Home number"

              />


            </div>



          </div>








          <div className="form-group">

            <label>
              Personal Phone
            </label>


            <input

              name="personalPhone"

              value={formData.personalPhone}

              onChange={handleChange}

              placeholder="Personal number"

            />


          </div>









          <div className="form-actions">





            <button

              type="button"

              onClick={()=>
                navigate("/dashboard")
              }

            >

              Cancel

            </button>






            <button

              type="submit"

              className="auth-button"

              disabled={loading}

            >


              {

                loading

                ?

                "Saving..."

                :

                "Save Contact"

              }



            </button>



          </div>






        </form>





      </div>





    </div>



  );


}



export default AddContact;

