import { ImportObjects } from "@/04-features/import-objects";
import {
  getImportHistory,
  type ImportOperation,
} from "@/05-models/imports";
import { useErrorNotify } from "@/06-shared/lib/errorNotifier";
import { useDataUpdateVersion } from "@/06-shared/lib/data-updates";
import {
  DataTable,
  type DataTableColumn,
} from "@/06-shared/ui/data-table";
import { useCallback, useEffect, useState } from "react";
import styles from "./index.module.css";

const statusLabels: Record<ImportOperation["status"], string> = {
  IN_PROGRESS: "Выполняется",
  SUCCESS: "Успешно",
  FAILED: "Ошибка",
};

const columns: DataTableColumn<ImportOperation>[] = [
  { key: "id", title: "ID", render: (operation) => operation.id },
  {
    key: "status",
    title: "Статус",
    render: (operation) => statusLabels[operation.status],
  },
  { key: "username", title: "Пользователь", render: (operation) => operation.username },
  {
    key: "importedCount",
    title: "Добавлено объектов",
    render: (operation) => operation.importedCount ?? "-",
  },
];

export const ImportHistory = () => {
  const [operations, setOperations] = useState<ImportOperation[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const showError = useErrorNotify();
  const dataUpdateVersion = useDataUpdateVersion();

  const reload = useCallback(async () => {
    setIsLoading(true);

    try {
      setOperations(await getImportHistory());
    } catch (error) {
      showError(error);
    } finally {
      setIsLoading(false);
    }
  }, [showError]);

  useEffect(() => {
    void reload();
  }, [dataUpdateVersion, reload]);

  return (
    <section className={styles.feed}>
      <div className={styles.header}>
        <h2 className={styles.title}>История импортов</h2>
        <div className={styles.actions}>
          <button type="button" onClick={() => void reload()}>
            [обновить]
          </button>
          <ImportObjects onImported={() => void reload()}/>
        </div>
      </div>

      <div className={styles.content}>
        {isLoading && <p className={styles.message}>Загрузка истории импортов...</p>}
        {!isLoading && operations.length === 0 && (
          <p className={styles.message}>Операции импорта отсутствуют</p>
        )}
        {!isLoading && operations.length > 0 && (
          <DataTable
            items={operations}
            columns={columns}
            getRowKey={(operation) => operation.id}
          />
        )}
      </div>
    </section>
  );
};
