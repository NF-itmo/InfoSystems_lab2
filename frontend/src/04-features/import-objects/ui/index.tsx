import {
  importObjects,
  type ImportObjectType,
} from "@/05-models/imports";
import { useErrorNotify } from "@/06-shared/lib/errorNotifier";
import {
  PopupButton,
  PopupSelect,
  PopupWindow,
} from "@/06-shared/ui/popups";
import { useRef, useState } from "react";
import styles from "./index.module.css";

type Props = {
  onImported: () => void;
};

const typeOptions = [
  { value: "book-creatures", label: "Существа книги" },
  { value: "coordinates", label: "Координаты" },
  { value: "magic-cities", label: "Города" },
  { value: "rings", label: "Кольца" },
];

export const ImportObjects = ({ onImported }: Props) => {
  const [isOpen, setIsOpen] = useState(false);
  const [type, setType] = useState<ImportObjectType>("book-creatures");
  const [file, setFile] = useState<File | null>(null);
  const [isImporting, setIsImporting] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const showError = useErrorNotify();

  const close = () => {
    setIsOpen(false);
    setFile(null);

    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  const submit = async () => {
    if (!file || isImporting) {
      return;
    }

    setIsImporting(true);

    try {
      await importObjects(type, file);
      onImported();
      close();
    } catch (error) {
      showError(error);
    } finally {
      setIsImporting(false);
    }
  };

  return (
    <>
      <button type="button" onClick={() => setIsOpen(true)}>
        [импорт]
      </button>
      {isOpen && (
        <PopupWindow name="Импорт YAML" onClose={close}>
          <PopupSelect
            name="Тип объектов"
            value={type}
            options={typeOptions}
            setValue={(value) => setType(value as ImportObjectType)}
          />
          <label className={styles.fileField}>
            <span>YAML-файл</span>
            <input
              ref={fileInputRef}
              type="file"
              accept=".yaml,.yml,application/yaml,text/yaml"
              onChange={(event) => setFile(event.target.files?.[0] ?? null)}
            />
          </label>
          {file && <p className={styles.fileName}>{file.name}</p>}
          <PopupButton
            name={isImporting ? "[импорт...]" : "[загрузить]"}
            onClick={() => void submit()}
          />
        </PopupWindow>
      )}
    </>
  );
};
