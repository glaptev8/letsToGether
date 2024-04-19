<template>
  <v-card-title v-if="!successMessage" class="text-h5">Profile</v-card-title>
  <v-card-subtitle>Tell everyone about you</v-card-subtitle>
  <div class="avatar-preview" v-if="previewAvatar">
    <img :src="previewAvatar" alt="Предварительный просмотр аватара" class="preview-image">
  </div>
  <v-card-text>
    <v-form v-if="!successMessage" ref="form" v-model="valid" @submit.prevent="submit">
      <v-text-field
        label="First Name"
        v-model="user.firstName"
        disabled
        required
      ></v-text-field>

      <v-text-field
        label="Last Name"
        v-model="user.lastName"
        disabled
        required
      ></v-text-field>

      <v-text-field
        label="Email"
        v-model="user.email"
        disabled
        required
      ></v-text-field>

      <v-select
        label="Gender"
        v-model="user.gender"
        :items="genders"
        :rules="[v => !!v || 'Item is required']"
        required
      ></v-select>

      <v-text-field
        label="Age"
        v-model="user.age"
        type="number"
        required
      ></v-text-field>

      <v-text-field
        label="tell about you"
        v-model="user.aboutMe"
        required
      ></v-text-field>

      <v-select
        label="hobbies"
        v-model="user.hobbies"
        multiple
        :items="hobbyTypes"
        :rules="[v => !!v || 'Item is required']"
        required
      ></v-select>

      <v-text-field
        label="Phone"
        v-model="user.phone"
        required
      ></v-text-field>
      <v-file-input
        label="Avatar"
        v-model="avatar"
        placeholder="Upload your avatar"
        prepend-icon="mdi-camera"
        accept="image/png, image/jpeg, image/bmp"
      ></v-file-input>

      <v-card-text>
        <v-alert v-if="errorMessages" type="error" dense dismissible>
          {{ errorMessages }}
        </v-alert>
      </v-card-text>
      <v-btn color="primary" :disabled="!valid" type="submit">
        save
      </v-btn>
    </v-form>
    <v-card-text>
      <v-alert v-if="successMessage" type="success" dense dismissible>
        {{ successMessage }}
      </v-alert>
    </v-card-text>
  </v-card-text>
</template>

<script setup>
import { ref, watch } from 'vue';
import axios from 'axios';
import { staticData } from '@/stores/static';
import { authData } from '@/stores/auth';

const errorMessages = ref(null);
const successMessage = ref(null);
const valid = ref(true);
const user = ref({
  firstName: '',
  lastName: '',
  email: '',
  hobbies: [],
  aboutMe: '',
  gender: '',
  age: null,
  phone: '',
})
const avatar = ref(null);
const previewAvatar = ref(null);
const genders = staticData().genderTypes;
const hobbyTypes = staticData().hobbyTypes;

const nameRules = [v => !!v || 'Name is required'];
const emailRules = [
  v => !!v || 'E-mail is required',
  v => /.+@.+\..+/.test(v) || 'E-mail must be valid',
];
const ageRules = [
  v => !!v || 'Age is required',
  v => v >= 18 || 'You must be at least 18 years old',
];
const avatarRules = [
  v => !v || v.size < 20000000000 || 'Avatar size should be less than 2 MB',
];

const submit = () => {
  if (valid.value) {
    const formData = new FormData();
    formData.append('firstName', user.value.firstName);
    formData.append('lastName', user.value.lastName);
    formData.append('email', user.value.email);
    if (user.value.gender) {
      formData.append('gender', user.value.gender);
    }
    if (user.value.hobbies) {
      formData.append('hobbies', user.value.hobbies);
    }
    if (user.value.aboutMe) {
      formData.append('aboutMe', user.value.aboutMe);
    }
    if (user.value.age) {
      formData.append('age', user.value.age);
    }
    if (user.value.phone) {
      formData.append('phone', user.value.phone);
    }
    if (avatar.value) {
      formData.append('avatar', avatar.value[0]);
    }

    axios.post('/auth/v1/update', formData)
      .then(response => {
        successMessage.value = "Информация обновлена";
      })
      .catch(error => {
        console.error('failed:', error);
        if (error.response && error.response.data && error.response.data.body && error.response.data.body.message) {
          errorMessages.value = error.response.data.body.message;
        } else {
          errorMessages.value = "Произошла ошибка. Пожалуйста, попробуйте снова.";
        }
      });
  }
};

watch(avatar, (newValue) => {
  if (newValue && newValue.length) {
    previewAvatar.value = URL.createObjectURL(newValue[0]);
  } else {
    previewAvatar.value = null;
  }
});

onMounted(async () => {
  const userId = authData().userId
  const response = await axios.get(`/auth/v1/user/${userId}`);
  if (response.status === 200 && response.data) {
    user.value = {
      ...user.value, // сохраняем текущие значения
      ...response.data // обновляем данными с сервера
    };
  }
})
</script>

<style scoped>
.avatar-preview {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.preview-image {
  border-radius: 50%; /* Делает изображение круглым */
  width: 100px; /* Размеры могут быть изменены по вашему желанию */
  height: 100px;
  object-fit: cover; /* Обеспечивает корректное заполнение контента внутри круга */
}
</style>


