import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function Dashboard() {

  const navigate = useNavigate();

  const [contacts, setContacts] = useState([]);
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const pageSize = 6;


  const clearSession = () => {

    localStorage.removeItem("token");
    localStorage.removeItem("user");

  };


  const getUserFromStorage = () => {

    try {

      const storedUser =
        localStorage.getItem("user");


      return storedUser
        ? JSON.parse(storedUser)
        : null;


    } catch(error){

      clearSession();

      return null;

    }

  };


  const user = getUserFromStorage();


  useEffect(() => {

    fetchContacts();

  }, [page, search]);



  const fetchContacts = async () => {

    try {

      setLoading(true);
      setError("");


      const response = await api.get(
        "/contacts",
        {
          params:{
            search,
            page,
            size:pageSize
          }
        }
      );


      setContacts(
        response.data.content || []
      );

      setTotalPages(
        response.data.totalPages || 0
      );

      setTotalElements(
        response.data.totalElements || 0
      );


    } catch(err){


      if(err.response?.status === 401){

        clearSession();
        navigate("/login");
        return;

      }


      setError(
        err.response?.data?.error ||
        "Unable to load contacts."
      );


    } finally {

      setLoading(false);

    }

  };



  const handleSearch = (e)=>{

    setSearch(e.target.value);
    setPage(0);

  };



  const handleLogout = ()=>{

    clearSession();
    navigate("/login");

  };



return (

<div className="dashboard">


<header className="dashboard-header">


<div>

<h1>
Good to see you, {user?.firstName || "User"} 👋
</h1>


<p>
Manage your contacts from one place
</p>


</div>



<div className="dashboard-actions">


<button
className="profile-button"
onClick={()=>navigate("/profile")}
>
My Profile
</button>


<button
className="logout-button"
onClick={handleLogout}
>
Logout
</button>


</div>


</header>



<main className="dashboard-content">



<div className="contacts-header">


<div>

<h2>
Contacts
</h2>


<p>
Manage your personal and professional connections
</p>


</div>



<button
className="add-contact-button"
onClick={()=>navigate("/contacts/new")}
>
+ Add Contact
</button>



</div>




<div className="dashboard-stats">


<div className="card stat-card">

<span>
Total Contacts
</span>

<strong>
{totalElements}
</strong>

</div>


</div>




<div className="contacts-toolbar">


<input

className="search-input"

type="text"

placeholder="Search contacts..."

value={search}

onChange={handleSearch}

/>



</div>




{loading && (

<div className="loading-state">

<p>
Loading contacts...
</p>

</div>

)}




{error && (

<div className="error-message">

{error}

</div>

)}






{
!loading &&
!error &&
contacts.length===0 &&

<div className="empty-state">


<h3>

{search
?
"No Contacts Found"
:
"No Contacts Yet"}

</h3>



<p>

{
search
?
"No matching contacts found."
:
"Start building your contact list."
}

</p>



{
!search &&

<button
onClick={()=>navigate("/contacts/new")}
>

Add Your First Contact

</button>

}


</div>

}






{
!loading &&
!error &&
contacts.length>0 &&


<div className="contacts-grid">


{

contacts.map(contact=>(


<div
className="contact-card"
key={contact.id}
>


<h3>

{contact.firstName} {contact.lastName}

</h3>



<p>

{contact.title || "No title"}

</p>



<p>

📧 {contact.workEmail || "N/A"}

</p>



<p>

📞 {contact.workPhone || "N/A"}

</p>



<button

onClick={()=>
navigate(`/contacts/${contact.id}`)
}

>

View Details

</button>



</div>


))

}



</div>


}





{
totalPages>1 &&


<div className="pagination">


<button

disabled={page===0}

onClick={()=>setPage(page-1)}

>
Previous
</button>



<span>

Page {page+1} of {totalPages}

</span>



<button

disabled={page>=totalPages-1}

onClick={()=>setPage(page+1)}

>

Next

</button>



</div>


}




</main>


</div>


);

}


export default Dashboard;