import axios from "axios";

// Typage du controller
export interface ControllerInfo {
  controller: string;
  method: string;
  url: string;
}

const API_BASE_URL = "http://localhost:8081/api/admin";

class ControllerService {
  // Récupérer la liste des controllers
  public async getControllers(): Promise<ControllerInfo[]> {
    const response = await axios.get<ControllerInfo[]>(`${API_BASE_URL}/controllers`);
    return response.data; // retourner directement les données
  }
}

export default new ControllerService();
