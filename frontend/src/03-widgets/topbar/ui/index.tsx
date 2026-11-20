import { LoginPopup } from "@/04-features/login-popup";
import { RegisterPopup } from "@/04-features/register-popup";
import { logout, validateSession } from "@/05-models/auth";
import { useErrorNotify } from "@/06-shared/lib/errorNotifier";
import { useEffect, useState } from "react";
import styles from "./index.module.css";

type Props = {
  onAuthenticationChange: (isAuthenticated: boolean) => void;
};

export const Topbar = ({ onAuthenticationChange }: Props) => {
  const [isCheckingSession, setIsCheckingSession] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoginOpen, setIsLoginOpen] = useState(false);
  const [isRegisterOpen, setIsRegisterOpen] = useState(false);
  const showError = useErrorNotify();

  const setAuthentication = (value: boolean) => {
    setIsAuthenticated(value);
    onAuthenticationChange(value);
  };

  useEffect(() => {
    const checkSession = async () => {
      try {
        await validateSession();
        setAuthentication(true);
      } catch {
        setAuthentication(false);
      } finally {
        setIsCheckingSession(false);
      }
    };

    void checkSession();
  }, []);

  const signOut = async () => {
    try {
      await logout();
      setAuthentication(false);
    } catch (error) {
      showError(error);
    }
  };

  return (
    <header className={styles.topbarWrapper}>
      <p className={styles.logoText}>TBD</p>
      <div/>
      <nav className={styles.navSection}>
        {!isCheckingSession && isAuthenticated && (
          <p className={styles.navOption} onClick={() => void signOut()}>
            [выйти]
          </p>
        )}
        {!isCheckingSession && !isAuthenticated && (
          <>
            <p className={styles.navOption} onClick={() => setIsLoginOpen(true)}>
              [войти]
            </p>
            <p className={styles.navOption} onClick={() => setIsRegisterOpen(true)}>
              [регистрация]
            </p>
          </>
        )}
      </nav>

      <LoginPopup
        isOpen={isLoginOpen}
        onClose={() => setIsLoginOpen(false)}
        onSuccess={() => setAuthentication(true)}
      />
      <RegisterPopup
        isOpen={isRegisterOpen}
        onClose={() => setIsRegisterOpen(false)}
        onSuccess={() => setAuthentication(true)}
      />
    </header>
  );
};
