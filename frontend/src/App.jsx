import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import AddContact from "./pages/AddContact";
import ContactDetails from "./pages/ContactDetails";
import EditContact from "./pages/EditContact";
import Profile from "./pages/Profile";

function App() {
  return (
    <BrowserRouter>

      <Routes>

        {/* Default route */}
        <Route
          path="/"
          element={
            <Navigate
              to="/login"
              replace
            />
          }
        />

        {/* Authentication */}
        <Route
          path="/login"
          element={<Login />}
        />

        <Route
          path="/register"
          element={<Register />}
        />

        {/* Dashboard */}
        <Route
          path="/dashboard"
          element={<Dashboard />}
        />

        {/* Future contact routes */}
       <Route
  path="/contacts/new"
  element={<AddContact />}
/>

        <Route
  path="/contacts/:id"
  element={<ContactDetails />}
/>
<Route
  path="/dashboard"
  element={<Dashboard />}
/>

<Route
  path="/contacts/new"
  element={<AddContact />}
/>

<Route
  path="/contacts/:id"
  element={<ContactDetails />}
/>
<Route
  path="/profile"
  element={<Profile />}
/>
<Route
  path="/contacts/:id/edit"
  element={<EditContact />}
/>
        {/* Unknown routes */}
        <Route
          path="*"
          element={
            <Navigate
              to="/login"
              replace
            />
          }
        />

      </Routes>

    </BrowserRouter>
  );
}

export default App;