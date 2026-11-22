import React, { useState, useEffect, useRef } from 'react';
import { roomService } from '../../services/room.service';
import { aiService } from '../../services/ai.service';
import { MapMarker, AiMapResponse } from '../../types';
import {
  MapPin,
  Sparkles,
  Loader2,
  ArrowRight,
  Search,
  LocateFixed,
  RefreshCw,
  X,
  Compass,
  Navigation,
  School,
  Building2,
  ChevronRight,
  Info
} from 'lucide-react';
import { Link } from 'react-router-dom';
import L from 'leaflet';

interface PresetLocation {
  name: string;
  address: string;
  lat: number;
  lng: number;
  type: 'university' | 'district' | 'city' | 'street' | 'other';
}

const PRESET_LOCATIONS: PresetLocation[] = [
  { name: 'Đại học Thương Mại (TMU)', address: '79 Hồ Tùng Mậu, Mai Dịch, Cầu Giấy', lat: 21.0366, lng: 105.7745, type: 'university' },
  { name: 'Đại học Công Nghiệp Hà Nội (HaUI)', address: '298 Cầu Diễn, Bắc Từ Liêm', lat: 21.0534, lng: 105.7355, type: 'university' },
  { name: 'Đại học Quốc Gia Hà Nội (VNU)', address: '144 Xuân Thủy, Cầu Giấy', lat: 21.0382, lng: 105.7827, type: 'university' },
  { name: 'Đại học Sư Phạm Hà Nội (HNUE)', address: '136 Xuân Thủy, Cầu Giấy', lat: 21.0371, lng: 105.7836, type: 'university' },
  { name: 'Đại học Bách Khoa Hà Nội (HUST)', address: 'Số 1 Đại Cồ Việt, Hai Bà Trưng', lat: 21.0044, lng: 105.8433, type: 'university' },
  { name: 'Đại học Kinh Tế Quốc Dân (NEU)', address: '207 Giải Phóng, Hai Bà Trưng', lat: 21.0007, lng: 105.8427, type: 'university' },
  { name: 'Đại học Ngoại Thương (FTU)', address: '91 Chùa Láng, Đống Đa', lat: 21.0232, lng: 105.8032, type: 'university' },
  { name: 'Quận Cầu Giấy', address: 'Khu vực Cầu Giấy, Hà Nội', lat: 21.0333, lng: 105.7933, type: 'district' },
  { name: 'Quận Bắc Từ Liêm', address: 'Khu vực Bắc Từ Liêm, Hà Nội', lat: 21.0667, lng: 105.7667, type: 'district' },
  { name: 'Quận Đống Đa', address: 'Khu vực Đống Đa, Hà Nội', lat: 21.0167, lng: 105.8250, type: 'district' },
  { name: 'Quận Hai Bà Trưng', address: 'Khu vực Hai Bà Trưng, Hà Nội', lat: 21.0050, lng: 105.8550, type: 'district' },
];

export const MapViewPage: React.FC = () => {
  const mapContainerRef = useRef<HTMLDivElement>(null);
  const mapInstanceRef = useRef<L.Map | null>(null);
  const markersLayerRef = useRef<L.LayerGroup | null>(null);
  const searchPinLayerRef = useRef<L.LayerGroup | null>(null);

  const [markers, setMarkers] = useState<MapMarker[]>([]);
  const [allOriginalMarkers, setAllOriginalMarkers] = useState<MapMarker[]>([]);
  const [selectedMarker, setSelectedMarker] = useState<MapMarker | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Standard Google Maps style search & live Geocoding
  const [searchLocation, setSearchLocation] = useState('');
  const [showLocationSuggestions, setShowLocationSuggestions] = useState(false);
  const [geocodeSuggestions, setGeocodeSuggestions] = useState<PresetLocation[]>([]);
  const [isSearchingGeocode, setIsSearchingGeocode] = useState(false);
  const [searchNotice, setSearchNotice] = useState<{ locationName: string; message: string } | null>(null);

  // AI Map Assistant modal / bottom drawer state
  const [isAiDrawerOpen, setIsAiDrawerOpen] = useState(false);
  const [aiPrompt, setAiPrompt] = useState('');
  const [isAiSearching, setIsAiSearching] = useState(false);
  const [aiResponse, setAiResponse] = useState<AiMapResponse | null>(null);

  const defaultCenter: [number, number] = [21.0285, 105.8542];

  useEffect(() => {
    if (!mapContainerRef.current || mapInstanceRef.current) return;

    const map = L.map(mapContainerRef.current, {
      zoomControl: false,
    }).setView(defaultCenter, 13);
    mapInstanceRef.current = map;

    L.control.zoom({ position: 'bottomright' }).addTo(map);

    // Reliable Google Maps & Esri layers
    const googleStreetLayer = L.tileLayer('https://mt{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}', {
      attribution: '&copy; Google Maps',
      subdomains: ['0', '1', '2', '3'],
      maxZoom: 20,
    });

    const googleHybridLayer = L.tileLayer('https://mt{s}.google.com/vt/lyrs=y&x={x}&y={y}&z={z}', {
      attribution: '&copy; Google Maps',
      subdomains: ['0', '1', '2', '3'],
      maxZoom: 20,
    });

    const esriStreetLayer = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
      attribution: '&copy; Esri &mdash; OpenStreetMap contributors',
      maxZoom: 19,
    });

    googleStreetLayer.addTo(map);

    L.control.layers(
      {
        'Google Maps (Đường phố)': googleStreetLayer,
        'Google Maps (Vệ tinh)': googleHybridLayer,
        'Bản đồ Esri Street': esriStreetLayer,
      },
      undefined,
      { position: 'bottomright' }
    ).addTo(map);

    const layerGroup = L.layerGroup().addTo(map);
    markersLayerRef.current = layerGroup;

    const searchPinGroup = L.layerGroup().addTo(map);
    searchPinLayerRef.current = searchPinGroup;

    const timer = setTimeout(() => {
      map.invalidateSize();
    }, 200);

    loadMarkers();

    const handleResize = () => map.invalidateSize();
    window.addEventListener('resize', handleResize);

    return () => {
      clearTimeout(timer);
      window.removeEventListener('resize', handleResize);
      map.remove();
      mapInstanceRef.current = null;
    };
  }, []);

  const loadMarkers = async () => {
    setIsLoading(true);
    try {
      const data = await roomService.getMapMarkers();
      const list = data || [];
      setMarkers(list);
      setAllOriginalMarkers(list);
      renderMarkers(list);
      if (list.length > 0 && mapInstanceRef.current) {
        fitToMarkers(list);
      }
    } catch (err) {
      console.error('Failed to load map markers:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const fitToMarkers = (markerList: MapMarker[]) => {
    if (!mapInstanceRef.current) return;
    const validPoints = markerList
      .filter((m) => m.latitude && m.longitude)
      .map((m) => [Number(m.latitude), Number(m.longitude)] as [number, number]);

    if (validPoints.length > 0) {
      const bounds = L.latLngBounds(validPoints);
      mapInstanceRef.current.fitBounds(bounds, { padding: [60, 60], maxZoom: 15 });
    }
  };

  const renderMarkers = (markerList: MapMarker[]) => {
    if (!mapInstanceRef.current || !markersLayerRef.current) return;

    markersLayerRef.current.clearLayers();

    markerList.forEach((item) => {
      if (item.latitude && item.longitude) {
        const priceMillions = (Number(item.price) / 1000000).toFixed(1).replace('.0', '');
        const customIcon = L.divIcon({
          className: 'custom-map-marker',
          html: `
            <div style="
              background-color: #2563eb;
              color: white;
              border-radius: 9999px;
              padding: 4px 10px;
              font-weight: 700;
              font-size: 12px;
              box-shadow: 0 4px 8px -1px rgba(0,0,0,0.3);
              border: 2px solid white;
              white-space: nowrap;
              cursor: pointer;
              display: flex;
              align-items: center;
              gap: 4px;
            ">
              <span>🏠</span><span>${priceMillions} tr</span>
            </div>
          `,
          iconSize: [68, 28],
          iconAnchor: [34, 14],
        });

        const marker = L.marker([Number(item.latitude), Number(item.longitude)], { icon: customIcon });
        marker.on('click', () => {
          setSelectedMarker(item);
        });
        markersLayerRef.current?.addLayer(marker);
      }
    });
  };

  // 1. Debounced geocoding search for Google Maps style address lookup
  useEffect(() => {
    const query = searchLocation.trim();
    if (!query || query.length < 2) {
      setGeocodeSuggestions([]);
      return;
    }

    const timer = setTimeout(async () => {
      setIsSearchingGeocode(true);
      try {
        const url = `https://photon.komoot.io/api/?q=${encodeURIComponent(query)}&limit=5&lang=vi`;
        const res = await fetch(url);
        if (res.ok) {
          const data = await res.json();
          if (data && data.features) {
            const list: PresetLocation[] = data.features.map((f: any) => {
              const p = f.properties || {};
              const coords = f.geometry?.coordinates || [0, 0];
              const parts = [p.name, p.street, p.locality, p.city, p.country].filter(Boolean);
              const address = parts.slice(1).join(', ') || p.country || 'Việt Nam';
              let type: 'university' | 'district' | 'city' | 'street' | 'other' = 'street';
              if (p.osm_value === 'university' || p.osm_value === 'school' || p.osm_value === 'college') {
                type = 'university';
              } else if (p.osm_value === 'administrative' || p.osm_key === 'boundary') {
                type = 'city';
              }
              return {
                name: p.name || query,
                address: address,
                lat: coords[1],
                lng: coords[0],
                type: type,
              };
            });
            setGeocodeSuggestions(list);
          }
        }
      } catch (err) {
        console.error('Geocode search error:', err);
      } finally {
        setIsSearchingGeocode(false);
      }
    }, 280);

    return () => clearTimeout(timer);
  }, [searchLocation]);

  // 2. Standard Google Maps style search handler
  const handleSelectLocation = (loc: PresetLocation) => {
    setSearchLocation(loc.name);
    setShowLocationSuggestions(false);
    setSearchNotice(null);

    if (mapInstanceRef.current) {
      const zoom = loc.type === 'city' ? 11 : loc.type === 'district' ? 13 : 15;
      mapInstanceRef.current.flyTo([loc.lat, loc.lng], zoom, { duration: 1.5 });

      // Add a distinct target pin
      if (searchPinLayerRef.current) {
        searchPinLayerRef.current.clearLayers();
        const pinIcon = L.divIcon({
          className: 'target-pin',
          html: `
            <div style="background-color: #dc2626; color: white; border-radius: 9999px; padding: 6px; border: 3px solid white; box-shadow: 0 4px 14px rgba(220,38,38,0.5); display: flex; align-items: center; justify-content: center; width: 36px; height: 36px; font-size: 16px;">
              📍
            </div>
          `,
          iconSize: [36, 36],
          iconAnchor: [18, 36],
        });
        const pin = L.marker([loc.lat, loc.lng], { icon: pinIcon })
          .bindPopup(`<b>${loc.name}</b><br><small>${loc.address}</small>`)
          .openPopup();
        searchPinLayerRef.current.addLayer(pin);
      }

      // Filter rooms within ~8km
      const nearby = allOriginalMarkers.filter((m) => {
        if (!m.latitude || !m.longitude) return false;
        const dist = calculateHaversineKm(loc.lat, loc.lng, Number(m.latitude), Number(m.longitude));
        return dist <= 8.0;
      });

      if (nearby.length > 0) {
        setMarkers(nearby);
        renderMarkers(nearby);
      } else {
        setMarkers([]);
        renderMarkers([]);
        setSearchNotice({
          locationName: loc.name,
          message: 'Hiện tại khu vực này chưa có phòng trọ nào được đăng trên TroFinder.',
        });
      }
    }
  };

  const handleStandardSearchSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchLocation.trim()) return;

    setShowLocationSuggestions(false);
    const lower = searchLocation.toLowerCase().trim();

    // 1. Check presets
    const matchedPreset = PRESET_LOCATIONS.find(
      (p) => p.name.toLowerCase().includes(lower) || p.address.toLowerCase().includes(lower)
    );
    if (matchedPreset) {
      handleSelectLocation(matchedPreset);
      return;
    }

    // 2. Check room titles/addresses in database
    const filtered = allOriginalMarkers.filter(
      (m) =>
        m.title.toLowerCase().includes(lower) ||
        m.address.toLowerCase().includes(lower)
    );
    if (filtered.length > 0) {
      setMarkers(filtered);
      renderMarkers(filtered);
      fitToMarkers(filtered);
      return;
    }

    // 3. Fallback: Geocode via Photon API across all of Vietnam (Provinces, Universities, Streets)
    try {
      const url = `https://photon.komoot.io/api/?q=${encodeURIComponent(searchLocation.trim())}&limit=1&lang=vi`;
      const res = await fetch(url);
      if (res.ok) {
        const data = await res.json();
        if (data && data.features && data.features.length > 0) {
          const f = data.features[0];
          const p = f.properties || {};
          const coords = f.geometry?.coordinates || [0, 0];
          const parts = [p.name, p.street, p.locality, p.city, p.country].filter(Boolean);
          const address = parts.slice(1).join(', ') || p.country || 'Việt Nam';
          const item: PresetLocation = {
            name: p.name || searchLocation,
            address: address,
            lat: coords[1],
            lng: coords[0],
            type: p.osm_value === 'university' ? 'university' : 'city',
          };
          handleSelectLocation(item);
          return;
        }
      }
    } catch (err) {
      console.error('Geocoding submit failed:', err);
    }

    setSearchNotice({
      locationName: searchLocation,
      message: 'Không tìm thấy toạ độ cho địa điểm này. Vui lòng kiểm tra lại chính tả hoặc thử địa chỉ cụ thể hơn.',
    });
  };

  // 2. AI Map Assistant search handler
  const handleAiMapSearch = async (promptText?: string) => {
    const textToSearch = promptText || aiPrompt;
    if (!textToSearch.trim()) return;

    setIsAiSearching(true);
    try {
      const res = await aiService.searchMapByAi(textToSearch);
      if (res) {
        setAiResponse(res);
        if (res.markers && res.markers.length > 0) {
          setMarkers(res.markers);
          renderMarkers(res.markers);
        }

        if (mapInstanceRef.current && res.centerLat && res.centerLng) {
          mapInstanceRef.current.flyTo(
            [Number(res.centerLat), Number(res.centerLng)],
            res.zoom || 14,
            { duration: 1.5 }
          );
        } else if (res.markers && res.markers.length > 0) {
          fitToMarkers(res.markers);
        }
      }
    } catch (err) {
      console.error('AI map search failed:', err);
      alert('Không thể kết nối trợ lý AI bản đồ lúc này. Vui lòng thử lại sau.');
    } finally {
      setIsAiSearching(false);
    }
  };

  const calculateHaversineKm = (lat1: number, lon1: number, lat2: number, lon2: number) => {
    const R = 6371;
    const dLat = ((lat2 - lat1) * Math.PI) / 180;
    const dLon = ((lon2 - lon1) * Math.PI) / 180;
    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos((lat1 * Math.PI) / 180) *
        Math.cos((lat2 * Math.PI) / 180) *
        Math.sin(dLon / 2) *
        Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  };

  const getImageSrc = (thumb?: string | null) => {
    if (!thumb) return 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=300&q=80';
    if (thumb.startsWith('http')) return thumb;
    return `http://localhost:8080${thumb}`;
  };

  const combinedSuggestions: PresetLocation[] = React.useMemo(() => {
    const list: PresetLocation[] = [];
    const seen = new Set<string>();

    PRESET_LOCATIONS.forEach((loc) => {
      const match =
        !searchLocation.trim() ||
        loc.name.toLowerCase().includes(searchLocation.toLowerCase()) ||
        loc.address.toLowerCase().includes(searchLocation.toLowerCase());
      if (match && !seen.has(loc.name.toLowerCase())) {
        seen.add(loc.name.toLowerCase());
        list.push(loc);
      }
    });

    geocodeSuggestions.forEach((loc) => {
      if (!seen.has(loc.name.toLowerCase())) {
        seen.add(loc.name.toLowerCase());
        list.push(loc);
      }
    });

    return list.slice(0, 8);
  }, [searchLocation, geocodeSuggestions]);

  return (
    <div className="relative h-[calc(100vh-4rem)] flex flex-col overflow-hidden">
      {/* 1. TOP BAR: Standard Google Maps style location search */}
      <div className="absolute top-4 left-1/2 -translate-x-1/2 z-[1000] w-full max-w-xl px-4">
        <div className="relative">
          <form
            onSubmit={handleStandardSearchSubmit}
            className="bg-white rounded-2xl shadow-xl border border-slate-200 p-2 flex items-center gap-2"
          >
            <div className="pl-2 text-slate-400">
              {isSearchingGeocode ? (
                <Loader2 className="w-5 h-5 text-blue-600 animate-spin" />
              ) : (
                <Search className="w-5 h-5 text-blue-600" />
              )}
            </div>
            <input
              type="text"
              value={searchLocation}
              onChange={(e) => {
                setSearchLocation(e.target.value);
                setShowLocationSuggestions(true);
              }}
              onFocus={() => setShowLocationSuggestions(true)}
              placeholder="Tìm địa điểm, tỉnh thành, trường ĐH (VD: Thái Bình, ĐH Thương Mại, Cầu Giấy)..."
              className="flex-1 px-2 py-1.5 text-xs sm:text-sm bg-transparent border-none focus:outline-none text-slate-800 placeholder-slate-400"
            />
            {searchLocation && (
              <button
                type="button"
                onClick={() => {
                  setSearchLocation('');
                  setSearchNotice(null);
                  setMarkers(allOriginalMarkers);
                  renderMarkers(allOriginalMarkers);
                  if (searchPinLayerRef.current) searchPinLayerRef.current.clearLayers();
                }}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X className="w-4 h-4" />
              </button>
            )}
            <button
              type="submit"
              className="bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold px-4 py-2 rounded-xl flex items-center gap-1 shrink-0 shadow-sm transition-all"
            >
              <span>Tìm kiếm</span>
            </button>
          </form>

          {/* Autocomplete suggestions dropdown */}
          {showLocationSuggestions && combinedSuggestions.length > 0 && (
            <div className="absolute top-full left-0 right-0 mt-2 bg-white rounded-2xl shadow-2xl border border-slate-200 overflow-hidden max-h-80 overflow-y-auto z-[1001]">
              <div className="p-2 border-b border-slate-100 flex items-center justify-between text-xs text-slate-400 font-semibold px-3">
                <span className="flex items-center gap-1.5">
                  <Compass className="w-3.5 h-3.5 text-blue-600" />
                  Gợi ý địa điểm & Trường ĐH (Toàn quốc)
                </span>
                <button
                  type="button"
                  onClick={() => setShowLocationSuggestions(false)}
                  className="hover:text-slate-600"
                >
                  Đóng
                </button>
              </div>
              {combinedSuggestions.map((loc, idx) => (
                <button
                  key={idx}
                  type="button"
                  onClick={() => handleSelectLocation(loc)}
                  className="w-full text-left px-4 py-2.5 hover:bg-blue-50 flex items-center gap-3 border-b border-slate-50 last:border-none transition-colors"
                >
                  <div className="w-8 h-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center shrink-0">
                    {loc.type === 'university' ? (
                      <School className="w-4 h-4" />
                    ) : loc.type === 'city' ? (
                      <Compass className="w-4 h-4" />
                    ) : loc.type === 'district' ? (
                      <Building2 className="w-4 h-4" />
                    ) : (
                      <MapPin className="w-4 h-4" />
                    )}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-xs font-bold text-slate-800 truncate">{loc.name}</p>
                    <p className="text-[11px] text-slate-500 truncate">{loc.address}</p>
                  </div>
                  <ChevronRight className="w-4 h-4 text-slate-300 shrink-0" />
                </button>
              ))}
            </div>
          )}

          {/* Notification Banner when navigated to an area */}
          {searchNotice && (
            <div className="mt-3 bg-white/95 backdrop-blur-md rounded-2xl shadow-xl border border-blue-200 p-3.5 flex items-start justify-between gap-3 text-xs animate-in fade-in slide-in-from-top-2">
              <div className="flex items-start gap-2.5">
                <div className="w-8 h-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center shrink-0 mt-0.5">
                  <MapPin className="w-4 h-4" />
                </div>
                <div>
                  <p className="font-bold text-slate-800">
                    Đã chuyển bản đồ đến: <span className="text-blue-600">{searchNotice.locationName}</span>
                  </p>
                  <p className="text-slate-600 mt-0.5">{searchNotice.message}</p>
                  <div className="flex items-center gap-3 mt-2">
                    <button
                      type="button"
                      onClick={() => {
                        setSearchNotice(null);
                        setMarkers(allOriginalMarkers);
                        renderMarkers(allOriginalMarkers);
                        fitToMarkers(allOriginalMarkers);
                        if (searchPinLayerRef.current) searchPinLayerRef.current.clearLayers();
                      }}
                      className="text-blue-600 hover:text-blue-700 font-semibold underline text-[11px]"
                    >
                      ← Xem tất cả phòng trọ (Hà Nội)
                    </button>
                  </div>
                </div>
              </div>
              <button
                type="button"
                onClick={() => setSearchNotice(null)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Map Header Status Overlay (Left Side) */}
      <div className="absolute top-4 left-4 z-[999] hidden lg:flex bg-white/95 backdrop-blur-md px-4 py-2.5 rounded-2xl shadow-lg border border-slate-200 items-center gap-3">
        <MapPin className="w-5 h-5 text-blue-600" />
        <div>
          <h2 className="text-xs font-bold text-slate-900">Bản đồ TroFinder</h2>
          <span className="text-[11px] text-slate-500">Đang ghim {markers.length} phòng trọ</span>
        </div>
        {isLoading && <Loader2 className="w-4 h-4 animate-spin text-blue-600 ml-1" />}
        <button
          onClick={loadMarkers}
          title="Tải lại danh sách phòng"
          className="p-1 text-slate-400 hover:text-blue-600 transition-colors"
        >
          <RefreshCw className="w-3.5 h-3.5" />
        </button>
        {markers.length > 0 && (
          <button
            onClick={() => fitToMarkers(markers)}
            title="Căn chỉnh toàn bộ phòng"
            className="p-1 text-slate-400 hover:text-blue-600 transition-colors"
          >
            <LocateFixed className="w-3.5 h-3.5" />
          </button>
        )}
      </div>

      {/* 2. MAP CONTAINER */}
      <div
        ref={mapContainerRef}
        style={{ height: 'calc(100vh - 4rem)', width: '100%', minHeight: '500px' }}
        className="w-full flex-1"
      />

      {/* 3. BOTTOM AI ASSISTANT BUTTON (Floating Action Button) */}
      <div className="absolute bottom-6 left-1/2 -translate-x-1/2 z-[1000] flex flex-col items-center gap-2">
        {!isAiDrawerOpen && (
          <button
            onClick={() => setIsAiDrawerOpen(true)}
            className="group bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white font-bold px-6 py-3.5 rounded-full shadow-2xl flex items-center gap-2.5 transition-all hover:scale-105 border-2 border-white/60"
          >
            <Sparkles className="w-5 h-5 text-yellow-300 animate-pulse" />
            <span className="text-sm">Hỏi Trợ lý AI Bản đồ</span>
            <span className="text-[10px] bg-white/20 text-white px-2 py-0.5 rounded-full font-medium hidden sm:inline">
              Tìm theo trường học & khoảng cách
            </span>
          </button>
        )}
      </div>

      {/* 4. AI MAP DRAWER / MODAL PANEL */}
      {isAiDrawerOpen && (
        <div className="absolute bottom-4 left-1/2 -translate-x-1/2 z-[1001] w-full max-w-2xl px-4 animate-in fade-in slide-in-from-bottom duration-300">
          <div className="bg-white rounded-3xl p-5 shadow-2xl border border-indigo-200 space-y-4 max-h-[85vh] overflow-y-auto">
            {/* Header */}
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <div className="flex items-center gap-2.5">
                <div className="w-9 h-9 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center font-bold">
                  <Sparkles className="w-5 h-5 text-indigo-600" />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-slate-900 flex items-center gap-2">
                    Trợ lý AI Bản đồ TroFinder
                    <span className="text-[10px] bg-indigo-100 text-indigo-700 px-2 py-0.5 rounded-full font-semibold">
                      Mô hình RAG Dữ liệu Thực tế
                    </span>
                  </h3>
                  <p className="text-[11px] text-slate-500">
                    Phân tích khoảng cách giữa các trường, gợi ý khu vực lý tưởng và ghim phòng trên bản đồ.
                  </p>
                </div>
              </div>
              <button
                onClick={() => setIsAiDrawerOpen(false)}
                className="w-8 h-8 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-500 flex items-center justify-center text-xs font-bold transition-colors"
              >
                ✕
              </button>
            </div>

            {/* Quick Prompt Chips */}
            <div className="space-y-1.5">
              <span className="text-[11px] font-semibold text-slate-400 block">Gợi ý câu hỏi phổ biến:</span>
              <div className="flex flex-wrap gap-2 text-xs">
                <button
                  type="button"
                  onClick={() => {
                    const q = 'Tìm phòng quanh khu vực trường đại học Thương Mại và đại học công nghiệp Hà Nội';
                    setAiPrompt(q);
                    handleAiMapSearch(q);
                  }}
                  className="px-3 py-1.5 rounded-xl bg-indigo-50/80 hover:bg-indigo-100 text-indigo-700 font-medium border border-indigo-100 text-left transition-colors flex items-center gap-1.5"
                >
                  <span>🎓</span>
                  <span>ĐH Thương Mại & ĐH Công Nghiệp</span>
                </button>
                <button
                  type="button"
                  onClick={() => {
                    const q = 'Tìm phòng gần ĐH Bách Khoa và ĐH Kinh Tế Quốc Dân dưới 4 triệu';
                    setAiPrompt(q);
                    handleAiMapSearch(q);
                  }}
                  className="px-3 py-1.5 rounded-xl bg-indigo-50/80 hover:bg-indigo-100 text-indigo-700 font-medium border border-indigo-100 text-left transition-colors flex items-center gap-1.5"
                >
                  <span>🎓</span>
                  <span>ĐH Bách Khoa & ĐH Kinh Tế Quốc Dân</span>
                </button>
                <button
                  type="button"
                  onClick={() => {
                    const q = 'Tìm chung cư mini quanh Cầu Giấy gần ĐH Quốc Gia có điều hòa';
                    setAiPrompt(q);
                    handleAiMapSearch(q);
                  }}
                  className="px-3 py-1.5 rounded-xl bg-indigo-50/80 hover:bg-indigo-100 text-indigo-700 font-medium border border-indigo-100 text-left transition-colors flex items-center gap-1.5"
                >
                  <span>🏢</span>
                  <span>CCMN Cầu Giấy gần ĐHQG</span>
                </button>
              </div>
            </div>

            {/* AI Search Form */}
            <form
              onSubmit={(e) => {
                e.preventDefault();
                handleAiMapSearch();
              }}
              className="flex gap-2"
            >
              <div className="relative flex-1">
                <input
                  type="text"
                  value={aiPrompt}
                  onChange={(e) => setAiPrompt(e.target.value)}
                  placeholder="Nhập yêu cầu: VD: Tôi muốn tìm phòng quanh ĐH Thương Mại và ĐH Công Nghiệp Hà Nội..."
                  className="w-full px-4 py-3 text-xs sm:text-sm bg-slate-50 border border-indigo-200 rounded-2xl focus:outline-none focus:ring-2 focus:ring-indigo-500 text-slate-800 placeholder-slate-400"
                />
              </div>
              <button
                type="submit"
                disabled={isAiSearching}
                className="bg-indigo-600 hover:bg-indigo-700 text-white text-xs sm:text-sm font-semibold px-5 py-3 rounded-2xl flex items-center gap-1.5 shrink-0 shadow-sm transition-all disabled:opacity-50"
              >
                {isAiSearching ? <Loader2 className="w-4 h-4 animate-spin" /> : <Sparkles className="w-4 h-4" />}
                <span>Hỏi AI</span>
              </button>
            </form>

            {/* AI Response Display */}
            {aiResponse && (
              <div className="p-4 rounded-2xl bg-gradient-to-br from-indigo-50/80 to-purple-50/50 border border-indigo-100 space-y-3">
                <div className="flex flex-wrap items-center justify-between gap-2">
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-bold text-indigo-900">
                      📍 Điểm mốc: {aiResponse.anchorName}
                    </span>
                    {aiResponse.distanceKm && (
                      <span className="text-[11px] font-semibold bg-indigo-200/80 text-indigo-800 px-2 py-0.5 rounded-full">
                        Khoảng cách: ~{aiResponse.distanceKm} km
                      </span>
                    )}
                  </div>
                  <span className="text-xs font-bold text-emerald-700 bg-emerald-100 px-2.5 py-0.5 rounded-full">
                    Ghim {aiResponse.totalFound} phòng trên bản đồ
                  </span>
                </div>

                {/* Recommended Areas Tags */}
                {aiResponse.recommendedAreas && aiResponse.recommendedAreas.length > 0 && (
                  <div className="flex items-center gap-1.5 flex-wrap">
                    <span className="text-[11px] font-semibold text-slate-500">Khu vực đề xuất ở giữa:</span>
                    {aiResponse.recommendedAreas.map((area, aIdx) => (
                      <span
                        key={aIdx}
                        className="text-[11px] font-medium bg-white text-indigo-700 px-2 py-0.5 rounded-lg border border-indigo-200 shadow-2xs"
                      >
                        {area}
                      </span>
                    ))}
                  </div>
                )}

                {/* Detailed Analysis Paragraph */}
                <div className="text-xs text-slate-700 leading-relaxed bg-white/80 p-3 rounded-xl border border-indigo-100">
                  <p className="whitespace-pre-line">{aiResponse.aiAnalysis}</p>
                </div>

                <div className="flex justify-end pt-1">
                  <button
                    type="button"
                    onClick={() => {
                      setIsAiDrawerOpen(false);
                      if (aiResponse.markers && aiResponse.markers.length > 0) {
                        fitToMarkers(aiResponse.markers);
                      }
                    }}
                    className="text-xs font-semibold text-indigo-600 hover:text-indigo-800 flex items-center gap-1"
                  >
                    Thu nhỏ để xem các phòng trên bản đồ <ChevronRight className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Selected Room Popup Drawer */}
      {selectedMarker && (
        <div className="absolute bottom-20 left-1/2 -translate-x-1/2 z-[1000] max-w-sm w-full mx-auto px-4">
          <div className="bg-white rounded-2xl p-4 shadow-2xl border border-slate-200 flex gap-3 relative">
            <button
              onClick={() => setSelectedMarker(null)}
              className="absolute top-2 right-2 w-6 h-6 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-500 flex items-center justify-center text-xs font-bold"
            >
              ✕
            </button>
            <div className="w-20 h-20 rounded-xl bg-slate-100 overflow-hidden shrink-0">
              <img
                src={getImageSrc(selectedMarker.thumbnail)}
                alt={selectedMarker.title}
                className="w-full h-full object-cover"
              />
            </div>
            <div className="flex-1 flex flex-col justify-between">
              <div>
                <span className="text-sm font-bold text-red-600 block">
                  {(Number(selectedMarker.price) / 1000000).toLocaleString('vi-VN')} tr/tháng
                </span>
                <h4 className="text-xs font-semibold text-slate-900 line-clamp-1">{selectedMarker.title}</h4>
                <p className="text-[11px] text-slate-500 line-clamp-1">{selectedMarker.address}</p>
              </div>
              <Link
                to={`/rooms/${selectedMarker.id}`}
                className="text-xs font-semibold text-blue-600 hover:text-blue-700 flex items-center gap-1 mt-1"
              >
                Xem chi tiết phòng <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
