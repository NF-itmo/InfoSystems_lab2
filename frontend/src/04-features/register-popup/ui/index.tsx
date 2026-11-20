import { register } from "@/05-models/auth";
import { useErrorNotify } from "@/06-shared/lib/errorNotifier";
import { PopupButton, PopupInput, PopupWindow } from "@/06-shared/ui/popups";
import { useState } from "react";

type Props = {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
};

export const RegisterPopup = ({
  isOpen,
  onClose,
  onSuccess,
}: Props) => {
  const [loginValue, setLoginValue] = useState("");
  const [password, setPassword] = useState("");
  const [inviteCode, setInviteCode] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const showError = useErrorNotify();

  const submit = async () => {
    if (isSubmitting) {
      return;
    }

    setIsSubmitting(true);

    try {
      await register({
        login: loginValue,
        password,
        inviteCode,
      });
      onSuccess();
      onClose();
    } catch (error) {
      showError(error);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!isOpen) {
    return null;
  }

  return (
    <PopupWindow name="Регистрация" onClose={onClose}>
      <PopupInput
        name="Логин"
        value={loginValue}
        setInput={setLoginValue}
        autoFocus
      />
      <PopupInput
        name="Пароль"
        value={password}
        setInput={setPassword}
      />
      <PopupInput
        name="Инвайт-код"
        value={inviteCode}
        setInput={setInviteCode}
        onEnter={submit}
      />
      <PopupButton
        name={isSubmitting ? "[регистрация...]" : "[зарегистрироваться]"}
        onClick={() => void submit()}
      />
    </PopupWindow>
  );
};
