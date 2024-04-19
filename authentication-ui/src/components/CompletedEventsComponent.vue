<template>
  <div class="events-container">
    <h3>Completed Events</h3>
    <ul v-if="completedEvents.length > 0">
      <li v-for="event in completedEvents" :key="event.id" class="event-card">
        <div @click="toggleDetails(event)" class="event-summary">
          <div class="left">
            <span>{{ event.name }} ({{ event.activityType }})</span>
          </div>
          <div class="right">
            <span class="rating">
              <span v-for="star in 5" :key="star" class="star" :class="{ 'filled': star <= event.averageGrade }">★</span>
            </span>
          </div>
          <v-icon style="margin-left: 3px">mdi-chevron-down</v-icon> <!-- Иконка для раскрытия отзывов -->
        </div>
        <p v-if="event.showDetails">{{ event.description }}</p>
        <div v-if="event.showDetails" class="review-section">
          <h5>Reviews:</h5>
          <ul class="reviews-list">
            <li v-for="review in event.reviews" :key="review.id">{{ review.review }}</li>
          </ul>
        </div>
      </li>
    </ul>
    <p v-else>No completed events found.</p>
  </div>
</template>


<script setup>
import { ref, onMounted, defineProps } from 'vue';
import axios from 'axios';
import { VIcon } from 'vuetify/components';


const completedEvents = ref([]);
const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
});

const loadCompletedEvents = async () => {
  try {
    const response = await axios.post('/event/v1/byfilter', {
      eventStatus: 'COMPLETED',
      userId: props.userId,
      own: true
    });
    completedEvents.value = response.data.map(event => ({
      ...event,
      showDetails: false,
      averageGrade: calculateAverageGrade(event.reviews)
    }));
    await Promise.all(completedEvents.value.map(loadReviews));
  } catch (error) {
    console.error('Error loading completed events:', error);
  }
};

const loadReviews = async (event) => {
  try {
    const response = await axios.post('/event/v1/all/review', { eventsIds: [event.id] });
    event.reviews = response.data;
    event.averageGrade = calculateAverageGrade(event.reviews);
  } catch (error) {
    console.error('Error loading reviews:', error);
  }
};

const calculateAverageGrade = (reviews) => {
  if (!reviews || reviews.length === 0) return 0;
  const total = reviews.reduce((acc, review) => acc + (review.grade || 0), 0);
  return Math.round(total / reviews.length);
};

const toggleDetails = (event) => {
  event.showDetails = !event.showDetails;
};

onMounted(() => {
  if (props.userId) {
    loadCompletedEvents();
  } else {
    console.log("User ID is not provided or invalid");
  }
});
</script>

<style scoped>
.rating .star {
  color: gold;
}

.star.filled {
  color: darkorange;
}

.events-container {
  max-width: 800px;
  margin: auto;
}

.event-card {
  border-bottom: 1px solid #ccc;
  padding: 10px;
  cursor: pointer;
}

.event-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.event-summary .left {
  flex: 1; /* Занимает всё доступное пространство, оставляя звёздочки и стрелочку вправо */
}

.event-summary .right {
  white-space: nowrap; /* Предотвращает перенос элементов на новую строку */
  display: flex;
  align-items: center;
}

.review-section {
  max-height: 150px;
  overflow-y: auto;
}

.review-section h5 {
  margin-top: 0;
}

.reviews-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.reviews-list li {
  background: #f8f8f8;
  border-radius: 5px;
  padding: 10px;
  margin-bottom: 5px;
}
</style>

