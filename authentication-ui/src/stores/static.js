import { defineStore } from 'pinia';
import axios from 'axios';


export const staticData = defineStore('static', {
  state: () => ({
    genderTypes: [],
    activityGroups: Map,
    eventStatuses: [],
    hobbyTypes: []
  }),
  actions: {
    fetchStatic() {
      const cachedGenders = localStorage.getItem('genderTypes')
      const activityGroups = localStorage.getItem('activityGroups')
      const hobbyTypes = localStorage.getItem('hobbyTypes')
      const eventStatuses = localStorage.getItem('eventStatuses')

      if (cachedGenders && activityGroups && hobbyTypes && eventStatuses) {
        // Если данные найдены, используем их и прекращаем выполнение функции
        this.genderTypes = JSON.parse(cachedGenders)
        this.activityGroups = JSON.parse(activityGroups)
        this.hobbyTypes = JSON.parse(hobbyTypes)
        this.eventStatuses = JSON.parse(eventStatuses)
        return;
      }

      axios.get('/static/v1')
        .then(response => {
          console.log(response)
          this.genderTypes = response.data.genderTypes;
          this.activityGroups = response.data.activityGroups;
          this.eventStatuses = response.data.eventStatuses;
          this.hobbyTypes = response.data.hobbyTypes;
          localStorage.setItem('genderTypes', JSON.stringify(response.data.genderTypes))
          localStorage.setItem('hobbyTypes', JSON.stringify(response.data.hobbyTypes))
          localStorage.setItem('activityGroups', JSON.stringify(response.data.genderTypes))
          localStorage.setItem('activityGroups', JSON.stringify(response.data.activityGroups))
        })
        .catch(error => {
          console.error('Ошибка при получении статического контента:', error)
        });
    }
  }
});
