export type ImportOperationStatus = "IN_PROGRESS" | "SUCCESS" | "FAILED";

export type ImportOperation = {
  id: number;
  status: ImportOperationStatus;
  username: string;
  importedCount: number | null;
};

export type ImportObjectType =
  | "book-creatures"
  | "coordinates"
  | "magic-cities"
  | "rings";

export type ImportObjectsResponse = {
  operationId: number;
  importedCount: number;
};
