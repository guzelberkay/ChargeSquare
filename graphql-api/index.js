const { ApolloServer, gql } = require('apollo-server');

// Şema: Sorguların ne olduğunu tanımlıyoruz
const typeDefs = gql`
    type Station {
        id: ID!
        name: String!
        location: Location!
        features: [String!]!
    }

    type Location {
        latitude: Float!
        longitude: Float!
    }

    type Query {
        stationsByLocation(latitude: Float!, longitude: Float!, radius: Int!): [Station!]
        stationsByFilter(name: String, features: [String!]): [Station!]
    }
`;

// Resolver: Sorgular nasıl çalışacak
const resolvers = {
    Query: {
        stationsByLocation: () => [
            { id: '1', name: 'Station 1', location: { latitude: 40, longitude: 29 }, features: ['Fast Charging'] },
        ],
        stationsByFilter: () => [
            { id: '2', name: 'Station 2', location: { latitude: 41, longitude: 28 }, features: ['Eco Charging'] },
        ],
    },
};

// Sunucu oluştur
const server = new ApolloServer({ typeDefs, resolvers });

// Sunucuyu başlat
server.listen().then(({ url }) => {
    console.log(`🚀 Server ready at ${url}`);
});
