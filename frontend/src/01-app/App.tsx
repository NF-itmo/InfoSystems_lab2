import { HomePage } from "@/02-pages/home";
import { Topbar } from "@/03-widgets/topbar";
import { ErrorNotifier } from "@/06-shared/lib/errorNotifier";
import { DataUpdatesProvider } from "@/06-shared/lib/data-updates";
import { useState } from "react";
import "./App.css";
import "./settings.css";

const App = () => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);

  return (
    <ErrorNotifier>
      <Topbar onAuthenticationChange={setIsAuthenticated}/>
      {isAuthenticated === null && (
        <main className="accessMessage">Проверка сессии...</main>
      )}
      {isAuthenticated === false && (
        <main className="accessMessage">
          Войдите или зарегистрируйтесь, чтобы продолжить.
        </main>
      )}
      {isAuthenticated === true && (
        <DataUpdatesProvider>
          <HomePage/>
        </DataUpdatesProvider>
      )}
    </ErrorNotifier>
  );
}

export default App;
