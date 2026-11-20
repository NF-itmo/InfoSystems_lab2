import { request } from "@/06-shared/api/request";
import { API_V1_PATH } from "@/06-shared/api/request/config";
import type {
  ImportObjectType,
  ImportObjectsResponse,
  ImportOperation,
} from "../model/types";

const IMPORTS_API_PATH = `${API_V1_PATH}/imports`;

export const getImportHistory = async (): Promise<ImportOperation[]> => {
  return request<ImportOperation[]>(`${IMPORTS_API_PATH}/history`);
};

export const importObjects = async (
  type: ImportObjectType,
  file: File,
): Promise<ImportObjectsResponse> => {
  const formData = new FormData();
  formData.append("file", file);

  return request<ImportObjectsResponse>(`${IMPORTS_API_PATH}/${type}`, {
    method: "POST",
    body: formData,
  });
};
