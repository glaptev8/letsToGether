import axios from 'axios';

export const fetchAvatarsByUserIds = async (userIds) => {
  try {
    const requestBody = {
      usersId: userIds, // убедитесь, что userIds это массив чисел
    };

    const response = await axios.post(`/api/auth/v1/users`, requestBody);
    return response.data; // Предполагается, что API возвращает массив объектов с информацией об аватарах
  } catch (error) {
    console.error("Ошибка при получении аватаров пользователей:", error);
    throw error; // Перебрасываем ошибку для дальнейшей обработки
  }
}

export const fetchUserIdsByEvent = async (eventId) => {
  try {
    const response = await axios.get(`/api/event/v1/users/event/${eventId}`);
    return response.data.map(user => user.userId);
  } catch (error) {
    console.error("Ошибка при получении данных пользователей:", error);
    throw error; // Перебрасываем ошибку для дальнейшей обработки
  }
}
