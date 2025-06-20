import API from "./api";

export interface PaymentType {
  code: string;
  label: string;
}

export const getAllPaymentTypes = async (): Promise<PaymentType[]> => {
  const response = await API.get("/payment-types");
  return response.data;
};
