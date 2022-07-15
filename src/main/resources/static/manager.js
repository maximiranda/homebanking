const {createApp} = Vue

createApp({
  data(){
    return {
      data: {},
      clients: [],
      lastName: "",
      firstName: "",
      email: "",
    }
  },
  created(){
    this.loadData()

  },
  methods: {
    loadData(){
      axios.get('/clients')
      .then(response => {
        // handle success
        this.data = response.data
        this.clients = this.data._embedded.clients
        
      })
      .catch(function (error) {
        // handle error
        console.log(error);
      })
    },
    addClient(){
      if(this.lastName != "" && this.firstName != "" && this.email.lenght != ""){
        this.postClient()
      }else {
        Swal.fire({
          icon: 'error',
          title: 'Oops...',
          text: 'Necesitas llenar todos los campos para poder continuar!',
        })
      }

    },
    postClient(){
      axios.post('/clients', {
        firstName: this.firstName,
        lastName: this.lastName,
        email: this.email,
        }).then((response) => {
              this.clients.push(response.data);
          }).catch(function (error) {
              console.log(error);
          });
    },
    deleteClient(client){
      const clientElement = document.getElementById(client)
      axios.delete(client, {})
      .then(()=> {
        clientElement.remove()
      })
    },

  }
}).mount("#app")

